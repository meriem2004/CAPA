package com.company.capa.service;

import com.company.capa.dto.CapaStatusResponse;
import com.company.capa.model.CAPA;
import com.company.capa.repository.CAPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CAPAService {
    private final CAPARepository capaRepository;
    private final WorkflowService workflowService;
    private final Zeebetaskservice zeebetaskservice;

    public CAPA create(CAPA capa) {
        String capaNumber = generateCapaNumber();
        capa.setCapaNumber(capaNumber);
        long piKey = workflowService.startCapaProcess(capa);
        capa.setProcessInstanceKey(piKey);
        return capaRepository.save(capa);
    }

    public CAPA get(Long id) {
        return capaRepository.findById(id).orElseThrow();
    }

    public List<CAPA> list() {
        return capaRepository.findAll();
    }

    public CAPA updateStatus(Long id, String status) {
        CAPA c = get(id);
        c.setCurrentStatus(status);
        return capaRepository.save(c);
    }

    public CAPA updateWorkflowVars(Long id, Boolean necessiteCapa, Boolean planApprouve, Integer rejectCount, Boolean efficace, Boolean besoinFormation) {
        CAPA c = get(id);
        if (necessiteCapa != null) c.setNecessiteCapa(necessiteCapa);
        if (planApprouve != null) c.setPlanApprouve(planApprouve);
        if (rejectCount != null) c.setRejectCount(rejectCount);
        if (efficace != null) c.setEfficace(efficace);
        if (besoinFormation != null) c.setBesoinFormation(besoinFormation);
        return capaRepository.save(c);
    }

    /**
     * Generate a unique CAPA number in the format: CAPA-YYYY-NNNN
     * where YYYY is the current year and NNNN is a sequential number
     */
    private String generateCapaNumber() {
        int currentYear = LocalDateTime.now().getYear();
        String yearPrefix = "CAPA-" + currentYear + "-";

        // Find the highest sequence number for the current year
        List<CAPA> capasThisYear = capaRepository.findAll().stream()
                .filter(c -> c.getCapaNumber() != null && c.getCapaNumber().startsWith(yearPrefix))
                .toList();

        int maxSequence = 0;
        for (CAPA c : capasThisYear) {
            try {
                String sequencePart = c.getCapaNumber().substring(yearPrefix.length());
                int sequence = Integer.parseInt(sequencePart);
                if (sequence > maxSequence) {
                    maxSequence = sequence;
                }
            } catch (Exception e) {
                // Skip invalid formats
            }
        }

        int nextSequence = maxSequence + 1;
        return String.format("%s%04d", yearPrefix, nextSequence);
    }

    /**
     * Get all CAPA instances with their current Camunda task status
     * Aggregates data from database and Camunda workflow engine
     */
    public List<CapaStatusResponse> getAllCapasWithStatus() {
        List<CAPA> capas = capaRepository.findAll();

        return capas.stream()
            .map(this::mapCapaToStatusResponse)
            .collect(Collectors.toList());
    }

    /**
     * Map a CAPA entity to CapaStatusResponse with Camunda task information
     */
    private CapaStatusResponse mapCapaToStatusResponse(CAPA capa) {
        CapaStatusResponse.CapaStatusResponseBuilder builder = CapaStatusResponse.builder()
            .id(capa.getId())
            .capaNumber(capa.getCapaNumber())
            .title(capa.getTitle())
            .description(capa.getDescription())
            .capaType(capa.getCapaType())
            .severity(capa.getSeverity())
            .currentStatus(capa.getCurrentStatus())
            .createdAt(capa.getCreatedAt())
            .dueDate(capa.getDueDate())
            .closedAt(capa.getClosedAt());

        // Add user information
        if (capa.getCreatedBy() != null) {
            builder.createdById(capa.getCreatedBy().getId())
                   .createdByUsername(capa.getCreatedBy().getUsername());
        }
        if (capa.getAssignedTo() != null) {
            builder.assignedToId(capa.getAssignedTo().getId())
                   .assignedToUsername(capa.getAssignedTo().getUsername());
        }

        // Get Camunda process instance information
        Long processInstanceKey = capa.getProcessInstanceKey();
        if (processInstanceKey != null) {
            builder.processInstanceKey(processInstanceKey);

            // Get process instance status from WorkflowService
            Map<String, Object> processStatus = workflowService.getProcessInstanceStatus(processInstanceKey);
            if (processStatus != null) {
                builder.processState((String) processStatus.get("state"))
                       .bpmnProcessId((String) processStatus.get("bpmnProcessId"))
                       .processVariables(processStatus);
            }

            // Get active tasks for this process instance from Zeebetaskservice
            log.debug("Querying tasks for CAPA ID: {}, Process Instance Key: {}", capa.getId(), processInstanceKey);
            List<Map<String, Object>> activeTasks = zeebetaskservice.getTasksForInstance(processInstanceKey);
            builder.activeTasks(activeTasks);
            log.debug("Found {} active tasks for CAPA ID: {}", activeTasks.size(), capa.getId());

            // Get current activity - try cache first (fast), then REST API (slower but accurate)
            String currentActivity = zeebetaskservice.getCurrentActivity(processInstanceKey);
            log.debug("Current activity from worker cache for CAPA ID {}: {}", capa.getId(), currentActivity);

            if (currentActivity == null) {
                // Cache doesn't have it - query REST API as fallback
                log.debug("Activity not in cache, querying Camunda REST API for CAPA ID: {}", capa.getId());
                currentActivity = workflowService.getCurrentActivityFromRestApi(processInstanceKey);
                log.debug("Current activity from REST API for CAPA ID {}: {}", capa.getId(), currentActivity);
            }

            if (currentActivity != null) {
                builder.currentActivity(currentActivity);
                // Extract current task information from active tasks
                if (!activeTasks.isEmpty()) {
                    Map<String, Object> currentTask = activeTasks.get(0);
                    builder.currentTaskName((String) currentTask.get("name"))
                           .currentTaskType((String) currentTask.get("type"));
                    log.debug("Set current activity to: {} for CAPA ID: {}", currentActivity, capa.getId());
                } else {
                    builder.currentTaskName(null)
                           .currentTaskType(null);
                }
            } else {
                // No activity found in cache or REST API - use CAPA's currentStatus as final fallback
                String fallbackStatus = capa.getCurrentStatus() != null ? capa.getCurrentStatus() : "Unknown";
                builder.currentActivity(fallbackStatus)
                       .currentTaskName(null)
                       .currentTaskType(null);
                log.debug("No activity found, using database status fallback: {} for CAPA ID: {}", fallbackStatus, capa.getId());
            }
        } else {
            // No process instance key - CAPA not started in workflow
            builder.processState("NOT_STARTED")
                   .currentActivity("Process not started")
                   .activeTasks(List.of());
        }

        return builder.build();
    }
}
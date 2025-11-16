package com.company.capa.service;

import com.company.capa.model.CAPA;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing Camunda workflow operations
 * This likely already exists in your codebase - if so, just add the missing methods
 */
@Service
@ConditionalOnBean(CamundaClient.class)
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final CamundaClient camundaClient;
    
    private static final String PROCESS_ID = "Process_CAPA_ISO";

    /**
     * Start a CAPA process instance
     * Returns the process instance key
     */
    public long startCapaProcess(CAPA capa) {
        log.info("Starting CAPA process for CAPA ID: {}, Number: {}", capa.getId(), capa.getCapaNumber());
        
        // Prepare process variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("capaId", capa.getId());
        variables.put("capaNumber", capa.getCapaNumber());
        variables.put("title", capa.getTitle());
        variables.put("description", capa.getDescription() != null ? capa.getDescription() : "");
        variables.put("capaType", capa.getCapaType());
        variables.put("severity", capa.getSeverity());
        
        // Workflow control variables
        variables.put("necessiteCapa", capa.getNecessiteCapa() != null ? capa.getNecessiteCapa() : false);
        variables.put("planApprouve", capa.getPlanApprouve() != null ? capa.getPlanApprouve() : false);
        variables.put("rejectCount", capa.getRejectCount() != null ? capa.getRejectCount() : 0);
        variables.put("efficace", capa.getEfficace() != null ? capa.getEfficace() : false);
        variables.put("besoinFormation", capa.getBesoinFormation() != null ? capa.getBesoinFormation() : false);
        
        try {
            ProcessInstanceEvent event = camundaClient
                .newCreateInstanceCommand()
                .bpmnProcessId(PROCESS_ID)
                .latestVersion()
                .variables(variables)
                .send()
                .join();
            
            long processInstanceKey = event.getProcessInstanceKey();
            log.info("✅ Started process instance with key: {} for CAPA: {}", 
                processInstanceKey, capa.getCapaNumber());
            
            return processInstanceKey;
            
        } catch (Exception e) {
            log.error("❌ Failed to start process for CAPA: {}", capa.getCapaNumber(), e);
            throw new RuntimeException("Failed to start CAPA process: " + e.getMessage(), e);
        }
    }

    /**
     * Update process variables for a running instance
     * Useful when CAPA data changes
     */
    public void updateProcessVariables(long processInstanceKey, Map<String, Object> variables) {
        log.info("Updating variables for process instance: {}", processInstanceKey);
        
        try {
            camundaClient
                .newSetVariablesCommand(processInstanceKey)
                .variables(variables)
                .send()
                .join();
            
            log.info("✅ Updated variables for process instance: {}", processInstanceKey);
            
        } catch (Exception e) {
            log.error("❌ Failed to update variables for process {}", processInstanceKey, e);
            throw new RuntimeException("Failed to update process variables: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel a process instance
     * Useful for closing/cancelling CAPAs
     */
    public void cancelProcess(long processInstanceKey) {
        log.info("Cancelling process instance: {}", processInstanceKey);
        
        try {
            camundaClient
                .newCancelInstanceCommand(processInstanceKey)
                .send()
                .join();
            
            log.info("✅ Cancelled process instance: {}", processInstanceKey);
            
        } catch (Exception e) {
            log.error("❌ Failed to cancel process {}", processInstanceKey, e);
            throw new RuntimeException("Failed to cancel process: " + e.getMessage(), e);
        }
    }

    /**
     * Publish a message to a process
     * Useful for signaling the process (e.g., approval received)
     */
    public void publishMessage(String messageName, String correlationKey, Map<String, Object> variables) {
        log.info("Publishing message '{}' with correlation key: {}", messageName, correlationKey);
        
        try {
            camundaClient
                .newPublishMessageCommand()
                .messageName(messageName)
                .correlationKey(correlationKey)
                .variables(variables != null ? variables : new HashMap<>())
                .send()
                .join();
            
            log.info("✅ Published message: {}", messageName);
            
        } catch (Exception e) {
            log.error("❌ Failed to publish message {}", messageName, e);
            throw new RuntimeException("Failed to publish message: " + e.getMessage(), e);
        }
    }
}
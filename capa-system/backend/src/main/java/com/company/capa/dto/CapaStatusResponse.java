package com.company.capa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapaStatusResponse {
    // CAPA basic information
    private Long id;
    private String capaNumber;
    private String title;
    private String description;
    private String capaType;
    private String severity;
    private String currentStatus;
    
    // Camunda process information
    private Long processInstanceKey;
    private String processState; // ACTIVE, COMPLETED, CANCELED, etc.
    private String currentActivity; // Current task/activity name
    private String bpmnProcessId;
    
    // Task information
    private List<Map<String, Object>> activeTasks; // List of active tasks/jobs
    private String currentTaskName; // Name of the current task
    private String currentTaskType; // Type of the current task
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private LocalDateTime closedAt;
    
    // User information
    private Long createdById;
    private String createdByUsername;
    private Long assignedToId;
    private String assignedToUsername;
    
    // Additional workflow information
    private Map<String, Object> processVariables; // Process variables from Camunda
}


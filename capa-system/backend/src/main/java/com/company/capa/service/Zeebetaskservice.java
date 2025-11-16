package com.company.capa.service;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobWorker;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for managing Zeebe user tasks
 * Polls for user tasks and provides methods to complete them
 * Follows the same pattern as AlertWorker and NotifyWorker
 */
@Service
@ConditionalOnBean(CamundaClient.class)
@RequiredArgsConstructor
@Slf4j
public class Zeebetaskservice {

    private final CamundaClient camundaClient;
    
    // In-memory cache of active tasks
    // In production, consider using Redis for distributed systems
    private final Map<Long, ActivatedJob> activeTasksCache = new ConcurrentHashMap<>();
    
    // Job worker instance
    private JobWorker userTaskWorker;

    /**
     * Start polling for user tasks when application starts
     */
    @PostConstruct
    public void start() {
        log.info("Starting user task worker...");
        
        try {
            userTaskWorker = camundaClient.newWorker()
                .jobType("io.camunda.zeebe:userTask") // Standard Zeebe user task type
                .handler((client, job) -> {
                    log.info("Received user task: {} (key: {}) for process instance: {}", 
                        job.getElementId(), 
                        job.getKey(),
                        job.getProcessInstanceKey());
                    
                    // Cache the task so frontend can query it
                    activeTasksCache.put(job.getKey(), job);
                    
                    log.debug("Task variables: {}", job.getVariablesAsMap());
                    log.info("Task cached. Total active tasks: {}", activeTasksCache.size());
                    
                    // Don't complete the job here - wait for frontend to complete it
                    // The job will timeout after 10 minutes if not completed
                })
                .timeout(Duration.ofMinutes(10))
                .name("user-task-worker")
                .maxJobsActive(100)
                .pollInterval(Duration.ofMillis(100))
                .open();
            
            log.info("✅ User task worker started successfully");
        } catch (Exception e) {
            log.error("❌ Failed to start user task worker", e);
            throw new RuntimeException("Failed to start user task worker", e);
        }
    }

    /**
     * Stop the worker when application shuts down
     */
    @PreDestroy
    public void stop() {
        if (userTaskWorker != null && !userTaskWorker.isClosed()) {
            log.info("Stopping user task worker...");
            try {
                userTaskWorker.close();
                log.info("User task worker stopped");
            } catch (Exception e) {
                log.warn("Error closing user task worker", e);
            }
        }
    }

    /**
     * Get all tasks for a specific process instance
     */
    public List<Map<String, Object>> getTasksForInstance(Long processInstanceKey) {
        return activeTasksCache.values().stream()
            .filter(job -> job.getProcessInstanceKey() == processInstanceKey)
            .map(this::jobToMap)
            .collect(Collectors.toList());
    }

    /**
     * Get all active tasks
     */
    public List<Map<String, Object>> getAllActiveTasks() {
        return activeTasksCache.values().stream()
            .map(this::jobToMap)
            .collect(Collectors.toList());
    }

    /**
     * Complete a user task with variables
     */
    public void completeTask(Long taskKey, Map<String, Object> variables) {
        ActivatedJob job = activeTasksCache.get(taskKey);
        
        if (job == null) {
            log.warn("Task not found in cache: {}. It may have been already completed or timed out.", taskKey);
            throw new RuntimeException("Task not found: " + taskKey);
        }

        try {
            // Ensure variables is not null
            Map<String, Object> vars = variables != null ? variables : new HashMap<>();
            
            log.info("Completing task {} with variables: {}", taskKey, vars);
            
            camundaClient.newCompleteCommand(job.getKey())
                .variables(vars)
                .send()
                .join(); // Block until completion is confirmed
            
            // Remove from cache after successful completion
            activeTasksCache.remove(taskKey);
            
            log.info("✅ Successfully completed task {}. Remaining tasks: {}", 
                taskKey, activeTasksCache.size());
            
        } catch (Exception e) {
            log.error("❌ Error completing task {}: {}", taskKey, e.getMessage(), e);
            throw new RuntimeException("Failed to complete task: " + e.getMessage(), e);
        }
    }

    /**
     * Convert ActivatedJob to a Map for API response
     */
    private Map<String, Object> jobToMap(ActivatedJob job) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", job.getKey()); // Task key serves as ID
        map.put("name", job.getElementId()); // Task name from BPMN (e.g., "Task_Qualify")
        map.put("processInstanceKey", job.getProcessInstanceKey());
        map.put("processDefinitionKey", job.getProcessDefinitionKey());
        map.put("type", job.getType());
        map.put("variables", job.getVariablesAsMap());
        map.put("customHeaders", job.getCustomHeaders());
        map.put("retries", job.getRetries());
        map.put("deadline", job.getDeadline());
        
        // Extract custom headers for form metadata if available
        Map<String, String> headers = job.getCustomHeaders();
        if (headers != null && !headers.isEmpty()) {
            map.put("formKey", headers.get("io.camunda.zeebe:formKey"));
        }
        
        return map;
    }

    /**
     * Clear a task from cache (useful for debugging)
     */
    public void clearTask(Long taskKey) {
        activeTasksCache.remove(taskKey);
        log.info("Cleared task {} from cache", taskKey);
    }

    /**
     * Clear all tasks from cache
     */
    public void clearAllTasks() {
        int count = activeTasksCache.size();
        activeTasksCache.clear();
        log.info("Cleared {} tasks from cache", count);
    }

    /**
     * Get count of active tasks
     */
    public int getActiveTaskCount() {
        return activeTasksCache.size();
    }
}
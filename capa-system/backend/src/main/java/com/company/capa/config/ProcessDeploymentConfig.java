package com.company.capa.config;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.DeploymentEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(CamundaClient.class)
@RequiredArgsConstructor
@Slf4j
public class ProcessDeploymentConfig {

    private final CamundaClient camundaClient;

    @PostConstruct
    public void deploy() {
        try {
            DeploymentEvent event = camundaClient
                    .newDeployResourceCommand()
                    .addResourceFromClasspath("processes/capa_corrected.bpmn")
                    .send()
                    .join();

            log.info("Deployed BPMN resources. Deployment key: {}", event.getKey());
        } catch (Exception e) {
            log.error("Failed to deploy BPMN resources", e);
            throw new IllegalStateException("BPMN deployment failed: " + e.getMessage(), e);
        }
    }
}

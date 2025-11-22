package com.company.capa.config;

import io.camunda.client.CamundaClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(CamundaClient.class)
public class ProcessDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessDeployer.class);

    private final CamundaClient camundaClient;

    public ProcessDeployer(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    @PostConstruct
    public void deployBpmn() {
        try {
            camundaClient
                .newDeployResourceCommand()
                .addResourceFromClasspath("processes/capa_corrected.bpmn")
                .send()
                .join();

            LOG.info("Deployed BPMN resource 'processes/capa_corrected.bpmn' successfully.");
        } catch (Exception e) {
            LOG.warn("BPMN deployment failed (may already be deployed): {}", e.getMessage());
        }
    }
}


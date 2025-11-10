package com.company.capa.config;

import io.camunda.client.CamundaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class CamundaConfig {
    
    @Bean
    @ConditionalOnProperty(name = "camunda.client.mode", havingValue = "saas", matchIfMissing = true)
    public CamundaClient camundaClient(
            @Value("${camunda.client.cluster-id}") String clusterId,
            @Value("${camunda.client.auth.client-id}") String clientId,
            @Value("${camunda.client.auth.client-secret}") String clientSecret,
            @Value("${camunda.client.region}") String region
    ) {
        if (clientId == null || clientSecret == null || clusterId == null || region == null) {
            throw new IllegalStateException(
                "Missing Camunda SaaS credentials. Please set CAMUNDA_CLIENT_ID, CAMUNDA_CLIENT_SECRET, CAMUNDA_CLUSTER_ID and CAMUNDA_CLUSTER_REGION environment variables."
            );
        }

        return CamundaClient.newCloudClientBuilder()
                .withClusterId(clusterId)
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withRegion(region)
                .build();
    }
}
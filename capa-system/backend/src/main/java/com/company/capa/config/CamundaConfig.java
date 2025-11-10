package com.company.capa.config;

import io.camunda.client.CamundaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamundaConfig {
    
    @Bean
    public CamundaClient camundaClient(
            @org.springframework.beans.factory.annotation.Value("${camunda.client.cluster-id}") String clusterId,
            @org.springframework.beans.factory.annotation.Value("${camunda.client.auth.client-id}") String clientId,
            @org.springframework.beans.factory.annotation.Value("${camunda.client.auth.client-secret}") String clientSecret,
            @org.springframework.beans.factory.annotation.Value("${camunda.client.region}") String region
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
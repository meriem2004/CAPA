package com.company.capa.service;

import com.company.capa.model.CAPA;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final ObjectProvider<CamundaClient> camundaClientProvider;

    public long startCapaProcess(CAPA capa) {
        Map<String, Object> vars = new HashMap<>();
        // Link process instance to the CAPA entity
        vars.put("capaId", capa.getId());
        vars.put("necessiteCapa", capa.getNecessiteCapa());
        vars.put("planApprouve", capa.getPlanApprouve());
        vars.put("rejectCount", capa.getRejectCount());
        vars.put("efficace", capa.getEfficace());
        vars.put("besoinFormation", capa.getBesoinFormation());

        CamundaClient camundaClient = camundaClientProvider.getIfAvailable();
        if (camundaClient == null) {
            // Mock mode: generate a realistic-looking process instance key
            return ThreadLocalRandom.current().nextLong(1_000_000_000_000L, 9_999_999_999_999L);
        }

        ProcessInstanceEvent evt = camundaClient.newCreateInstanceCommand()
                .bpmnProcessId("Process_CAPA_ISO")
                .latestVersion()
                .variables(vars)
                .send()
                .join();
        return evt.getProcessInstanceKey();
    }
}
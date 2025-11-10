package com.company.capa.service;

import com.company.capa.model.CAPA;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final CamundaClient camundaClient;

    public long startCapaProcess(CAPA capa) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("necessiteCapa", capa.getNecessiteCapa());
        vars.put("planApprouve", capa.getPlanApprouve());
        vars.put("rejectCount", capa.getRejectCount());
        vars.put("efficace", capa.getEfficace());
        vars.put("besoinFormation", capa.getBesoinFormation());

        ProcessInstanceEvent evt = camundaClient.newCreateInstanceCommand()
                .bpmnProcessId("Process_CAPA_ISO")
                .latestVersion()
                .variables(vars)
                .send()
                .join();
        return evt.getProcessInstanceKey();
    }
}
package com.company.capa.worker;

import com.company.capa.service.NotificationService;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.api.worker.JobHandler;
import io.camunda.client.api.worker.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(CamundaClient.class)
@RequiredArgsConstructor
public class NotifyWorker implements JobHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NotifyWorker.class);

    private final CamundaClient camundaClient;
    private final NotificationService notificationService;

    private JobWorker worker;

    @PostConstruct
    public void start() {
        worker = camundaClient.newWorker()
                .jobType("notify-sla-breach")
                .handler(this)
                .name("notify-worker")
                .open();
    }

    @PreDestroy
    public void stop() {
        if (worker != null) {
            try {
                worker.close();
                LOG.info("NotifyWorker closed.");
            } catch (Exception e) {
                LOG.warn("Error closing NotifyWorker", e);
            }
        }
    }

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        Long capaId = job.getVariablesAsMap().containsKey("capaId") ? ((Number) job.getVariablesAsMap().get("capaId")).longValue() : null;
        notificationService.notifySlaBreach(capaId);
        client.newCompleteCommand(job.getKey()).send().join();
    }
}
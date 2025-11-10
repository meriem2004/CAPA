package com.company.capa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void notifySlaBreach(Long capaId) {
        log.info("Notify SLA breach for CAPA {}", capaId);
    }

    public void alertDeadline(Long capaId) {
        log.info("Alert deadline for CAPA {}", capaId);
    }
}
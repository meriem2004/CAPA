package com.company.capa.service;

import com.company.capa.model.CAPA;
import com.company.capa.repository.CAPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CAPAService {
    private final CAPARepository capaRepository;

    public CAPA create(CAPA capa) {
        return capaRepository.save(capa);
    }

    public CAPA get(Long id) {
        return capaRepository.findById(id).orElseThrow();
    }

    public List<CAPA> list() {
        return capaRepository.findAll();
    }

    public CAPA updateStatus(Long id, String status) {
        CAPA c = get(id);
        c.setCurrentStatus(status);
        return capaRepository.save(c);
    }

    public CAPA updateWorkflowVars(Long id, Boolean necessiteCapa, Boolean planApprouve, Integer rejectCount, Boolean efficace, Boolean besoinFormation) {
        CAPA c = get(id);
        if (necessiteCapa != null) c.setNecessiteCapa(necessiteCapa);
        if (planApprouve != null) c.setPlanApprouve(planApprouve);
        if (rejectCount != null) c.setRejectCount(rejectCount);
        if (efficace != null) c.setEfficace(efficace);
        if (besoinFormation != null) c.setBesoinFormation(besoinFormation);
        return capaRepository.save(c);
    }
}
package com.company.capa.controller;

import com.company.capa.model.CAPA;
import com.company.capa.service.CAPAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/capa")
@RequiredArgsConstructor
public class CAPAController {
    private final CAPAService capaService;

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<CAPA> create(@RequestBody @Valid CAPA capa) {
        CAPA created = capaService.create(capa);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<List<CAPA>> list() {
        return ResponseEntity.ok(capaService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<CAPA> get(@PathVariable Long id) {
        return ResponseEntity.ok(capaService.get(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<CAPA> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(capaService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/workflow")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<CAPA> updateWorkflow(@PathVariable Long id,
                                               @RequestParam(required = false) Boolean necessiteCapa,
                                               @RequestParam(required = false) Boolean planApprouve,
                                               @RequestParam(required = false) Integer rejectCount,
                                               @RequestParam(required = false) Boolean efficace,
                                               @RequestParam(required = false) Boolean besoinFormation) {
        return ResponseEntity.ok(capaService.updateWorkflowVars(id, necessiteCapa, planApprouve, rejectCount, efficace, besoinFormation));
    }
}
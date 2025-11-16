package com.company.capa.repository;

import com.company.capa.model.CapaDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CapaDocumentRepository extends JpaRepository<CapaDocument, Long> {
    Optional<CapaDocument> findByProcessInstanceId(Long processInstanceId);
    Optional<CapaDocument> findByCapaNumber(String capaNumber);
}
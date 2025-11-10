package com.company.capa.repository;

import com.company.capa.model.CAPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CAPARepository extends JpaRepository<CAPA, Long> {
    Optional<CAPA> findByCapaNumber(String capaNumber);
}
package com.company.capa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "action")
@Getter
@Setter
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "capa_id")
    private CAPA capa;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "action_type", length = 20)
    private String actionType; // immediate | corrective | preventive

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private User responsible;

    private LocalDate plannedStart;
    private LocalDate plannedEnd;
    private LocalDate actualStart;
    private LocalDate actualEnd;

    @Column(length = 20)
    private String status = "planned"; // planned | in_progress | completed

    @Column(name = "progress_percent")
    private Integer progressPercent = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
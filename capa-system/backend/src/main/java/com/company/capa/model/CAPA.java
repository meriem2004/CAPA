package com.company.capa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "capa")
@Getter @Setter
public class CAPA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "capa_number", nullable = false, unique = true, length = 20)
    @Size(max = 20, message = "capaNumber must be at most 20 characters")
    private String capaNumber;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "capa_type", nullable = false, length = 20)
    private String capaType; // corrective | preventive

    @Column(length = 20)
    private String severity; // minor | major | critical

    @Column(name = "process_instance_key")
    private Long processInstanceKey;

    @Column(name = "current_status", nullable = false, length = 50)
    private String currentStatus = "open"; // open, analysis, planned, execution, closed

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    // Workflow vars
    @Column(name = "necessite_capa")
    private Boolean necessiteCapa = false;

    @Column(name = "plan_approuve")
    private Boolean planApprouve = false;

    @Column(name = "reject_count")
    private Integer rejectCount = 0;

    @Column(name = "efficace")
    private Boolean efficace = false;

    @Column(name = "besoin_formation")
    private Boolean besoinFormation = false;
}
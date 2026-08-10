package emi_schedular.example.tss_evaluation2_emi_schedular_app.entity;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.AuditAction;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(
            name = "actor_type",
            nullable = false,
            length = 20
    )
    private Role actorType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "target_entity", length = 50)
    private String targetEntity;

    @Column(name = "target_id")
    private Long targetId;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}

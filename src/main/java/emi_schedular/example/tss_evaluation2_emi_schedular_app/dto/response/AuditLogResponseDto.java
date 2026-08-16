package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.AuditAction;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDto {

    private Long id;

    private AuditAction action;

    private Role actorRole;

    private String targetEntity;

    private Long targetId;

    private LocalDateTime timestamp;

    private Long actorId;

    private String actorName;
}

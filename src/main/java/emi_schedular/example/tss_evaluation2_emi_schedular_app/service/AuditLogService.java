package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.AuditLogResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.AuditAction;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {

    PageDto<AuditLogResponseDto> getAuditLogs(Pageable pageable);

    PageDto<AuditLogResponseDto> getLoanAuditLogs(Long loanId, Pageable pageable);

    void createAuditLog(User actor, AuditAction action, String targetEntity, Long targetId);
}

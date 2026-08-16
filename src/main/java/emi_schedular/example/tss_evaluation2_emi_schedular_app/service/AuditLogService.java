package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.AuditLogResponseDto;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {

    PageDto<AuditLogResponseDto> getAuditLogs(Pageable pageable);

    PageDto<AuditLogResponseDto> getLoanAuditLogs(Long loanId, Pageable pageable);
}

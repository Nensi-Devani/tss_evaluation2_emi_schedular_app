package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.AuditLogResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@Slf4j
public class AdminAuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<PageDto<AuditLogResponseDto>> getAuditLogs(Pageable pageable) {
        log.info(
                "Admin requested audit logs. page={}, size={}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return ResponseEntity.ok(auditLogService.getAuditLogs(pageable));
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<PageDto<AuditLogResponseDto>> getLoanAuditLogs(@PathVariable Long loanId, Pageable pageable) {
        log.info(
                "Admin requested audit trail for loan id={}",
                loanId
        );

        return ResponseEntity.ok(auditLogService.getLoanAuditLogs(loanId,pageable));
    }
}

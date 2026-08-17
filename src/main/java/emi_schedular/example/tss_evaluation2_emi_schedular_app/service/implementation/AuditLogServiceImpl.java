package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.AuditLogResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.AuditLog;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.AuditAction;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper.AuditLogMapper;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.AuditLogRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public PageDto<AuditLogResponseDto> getAuditLogs(Pageable pageable) {
        log.info(
                "Fetching audit logs. page={}, size={}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return new PageDto<>(
                auditLogRepository.findAllByOrderByTimestampDesc(pageable),
                auditLogMapper::toResponseDto
        );
    }

    @Override
    public PageDto<AuditLogResponseDto> getLoanAuditLogs(Long loanId, Pageable pageable) {
        log.info(
                "Fetching audit trail for loan id={}",
                loanId
        );

        Page<AuditLog> auditLogPage = auditLogRepository
                        .findByTargetEntityAndTargetIdOrderByTimestampAsc(
                                "LOAN",
                                loanId,
                                pageable
                        );

        return new PageDto<>(
                auditLogPage,
                auditLogMapper::toResponseDto
        );
    }

    @Override
    public void createAuditLog(User actor, AuditAction action, String targetEntity, Long targetId) {
        AuditLog auditLog = new AuditLog();

        auditLog.setActor(actor);
        auditLog.setAction(action);
        auditLog.setTargetEntity(targetEntity);
        auditLog.setTargetId(targetId);
        auditLog.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(auditLog);

        log.info(
                "Audit log created. action={}, targetEntity={}, targetId={}, actorId={}",
                action,
                targetEntity,
                targetId,
                actor.getId()
        );
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.AuditLogResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(source = "actor.id", target = "actorId")
    @Mapping(source = "actor.fullName", target = "actorName")
    @Mapping(source = "actor.role", target = "actorRole")
    AuditLogResponseDto toResponseDto(AuditLog auditLog);
}

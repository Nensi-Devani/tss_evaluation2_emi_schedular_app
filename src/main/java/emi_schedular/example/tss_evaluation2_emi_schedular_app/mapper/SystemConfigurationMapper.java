package emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateSystemConfigurationRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.SystemConfigurationResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.SystemConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SystemConfigurationMapper {

    SystemConfigurationResponseDto toResponseDto(SystemConfiguration configuration);

    void updateEntity(UpdateSystemConfigurationRequestDto request, @MappingTarget SystemConfiguration configuration);
}
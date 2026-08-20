package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateSystemConfigurationRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.SystemConfigurationResponseDto;
import org.springframework.data.domain.Pageable;

public interface AdminSystemConfigurationService {

    PageDto<SystemConfigurationResponseDto> getConfigurations(Pageable pageable);

    SystemConfigurationResponseDto getConfigurationByKey(String key);

    SystemConfigurationResponseDto updateConfiguration(String key, UpdateSystemConfigurationRequestDto request);
}

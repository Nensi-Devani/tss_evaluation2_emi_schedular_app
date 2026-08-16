package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateSystemConfigurationRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.SystemConfigurationResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.SystemConfiguration;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper.SystemConfigurationMapper;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.SystemConfigurationRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AdminSystemConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSystemSystemConfigurationServiceImpl implements AdminSystemConfigurationService {

    private final SystemConfigurationRepository configurationRepository;
    private final SystemConfigurationMapper configurationMapper;

    @Override
    @Transactional(readOnly = true)
    public PageDto<SystemConfigurationResponseDto> getConfigurations(Pageable pageable) {
        log.info(
                "Fetching configurations. page={}, size={}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<SystemConfiguration> configurationPage = configurationRepository.findAll(pageable);

        return new PageDto<>(
                configurationPage,
                configurationMapper::toResponseDto
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SystemConfigurationResponseDto getConfigurationByKey(String key) {
        log.info(
                "Fetching configuration. key={}",
                key
        );

        SystemConfiguration configuration = configurationRepository.findByConfigKey(key)
                                            .orElseThrow(() ->
                                                    new ResourceNotFoundException("Configuration not found with key: " + key)
                                            );

        return configurationMapper.toResponseDto(configuration);
    }

    @Override
    @Transactional
    public SystemConfigurationResponseDto updateConfiguration(String key, UpdateSystemConfigurationRequestDto request) {
        log.info(
                "Updating configuration. key={}",
                key
        );

        SystemConfiguration configuration = configurationRepository.findByConfigKey(key)
                                                .orElseThrow(() ->
                                                     new ResourceNotFoundException("Configuration not found with key: " + key)
                                                );

        configurationMapper.updateEntity(request, configuration);

        SystemConfiguration updatedConfiguration = configurationRepository.save(configuration);

        log.info(
                "Configuration updated successfully. key={}",
                key
        );

        return configurationMapper.toResponseDto(updatedConfiguration);
    }
}

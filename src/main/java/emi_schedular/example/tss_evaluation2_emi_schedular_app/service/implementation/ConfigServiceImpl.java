package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.SystemConfigResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.SystemConfiguration;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.SystemConfigurationRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigServiceImpl implements ConfigService {

    private final SystemConfigurationRepository configRepository;

    @Override
    public String getValue(String key) {
        return configRepository.findByConfigKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("System configuration '" + key + "'"))
                .getConfigValue();
    }

    @Override
    public BigDecimal getDecimal(String key) {
        return new BigDecimal(getValue(key));
    }

    @Override
    public int getInt(String key) {
        return Integer.parseInt(getValue(key));
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.SystemConfigResponseDto;

import java.math.BigDecimal;
import java.util.List;


// Simple wrapper to read admin-set values (D1, D2, T, S, P, min/max amount & tenure)
// straight from the system_configurations table.
public interface ConfigService {

    String getValue(String key);

    BigDecimal getDecimal(String key);

    int getInt(String key);
}

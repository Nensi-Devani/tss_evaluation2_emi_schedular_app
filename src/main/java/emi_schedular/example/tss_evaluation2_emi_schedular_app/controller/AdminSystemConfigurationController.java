package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateSystemConfigurationRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.SystemConfigurationResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AdminSystemConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/configurations")
@RequiredArgsConstructor
@Slf4j
public class AdminSystemConfigurationController {

    private final AdminSystemConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<PageDto<SystemConfigurationResponseDto>> getConfigurations(Pageable pageable) {
        return ResponseEntity.ok(configurationService.getConfigurations(pageable));
    }

    @GetMapping("/{key}")
    public ResponseEntity<SystemConfigurationResponseDto> getConfigurationByKey(@PathVariable String key) {
        return ResponseEntity.ok(configurationService.getConfigurationByKey(key));
    }

    @PutMapping("/{key}")
    public ResponseEntity<SystemConfigurationResponseDto> updateConfiguration(@PathVariable String key, @Valid @RequestBody UpdateSystemConfigurationRequestDto request) {
        SystemConfigurationResponseDto response = configurationService.updateConfiguration(key, request);

        return ResponseEntity.ok(response);
    }
}
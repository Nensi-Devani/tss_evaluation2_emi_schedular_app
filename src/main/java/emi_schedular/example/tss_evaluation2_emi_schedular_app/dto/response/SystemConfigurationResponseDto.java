package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SystemConfigurationResponseDto {

    private Long id;

    private String configKey;

    private String configValue;
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateSystemConfigurationRequestDto {

    @NotBlank(message = "Configuration value is required")
    private String configValue;
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDto {

    private String accessToken;
    private String tokenType;
    private String role;
}

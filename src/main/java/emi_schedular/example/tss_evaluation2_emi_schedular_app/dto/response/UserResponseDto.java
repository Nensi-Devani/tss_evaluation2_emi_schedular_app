package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String status;
}

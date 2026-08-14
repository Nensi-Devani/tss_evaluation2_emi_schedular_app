package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;
}

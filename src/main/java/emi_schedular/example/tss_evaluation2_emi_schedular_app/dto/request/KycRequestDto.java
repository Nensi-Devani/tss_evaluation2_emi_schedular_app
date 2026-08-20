package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycRequestDto {

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank
    @Size(max = 500)
    private String address;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotBlank
    @Size(max = 100)
    private String state;

    @NotBlank
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Enter a valid 6-digit PIN code")
    private String pinCode;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$", message = "Enter a valid PAN (e.g. ABCDE1234F)")
    private String pan;

    @NotBlank
    @Pattern(regexp = "^[0-9]{12}$", message = "Enter a valid 12-digit Aadhar number")
    private String aadhar;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be greater than 0")
    private BigDecimal monthlyIncome;

    @NotNull
    @DecimalMin(value = "0.0", message = "Existing monthly debt cannot be negative")
    private BigDecimal existingMonthlyDebt;
}

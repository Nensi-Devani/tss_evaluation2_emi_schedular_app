package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateUserFinancialProfileRequestDto {

    @NotNull(message = "monthlyIncome is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be greater than 0")
    private BigDecimal monthlyIncome;

    @NotNull(message = "existingMonthlyDebt is required")
    @DecimalMin(value = "0.0", message = "Existing monthly debt cannot be negative")
    private BigDecimal existingMonthlyDebt;
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequestDto {

    @NotNull(message = "loanAmount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "loanAmount must be greater than 0")
    private BigDecimal loanAmount;

    @NotNull(message = "tenure is required")
    @Min(value = 12, message = "tenure must be at least 12 month")
    private Integer tenure;

    @NotNull(message = "loanType is required")
    private LoanType loanType;

    // optional, kept for records only
    @Size(max = 255, message = "purpose must not exceed 255 characters")
    private String purpose;
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class UpdateLoanStrategyRequestDto {

    @NotNull(message = "Strategy is required")
    private LoanStrategy strategy;
}

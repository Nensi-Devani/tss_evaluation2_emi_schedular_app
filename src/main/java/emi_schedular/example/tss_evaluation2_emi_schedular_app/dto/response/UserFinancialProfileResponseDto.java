package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class UserFinancialProfileResponseDto {

    private String pan;

    private String aadhar;

    private BigDecimal monthlyIncome;

    private BigDecimal existingMonthlyDebt;
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFinancialProfileResponseDto {

    private String pan;
    private String aadhaar;
    private BigDecimal monthlyIncome;
    private BigDecimal existingMonthlyDebt;
}

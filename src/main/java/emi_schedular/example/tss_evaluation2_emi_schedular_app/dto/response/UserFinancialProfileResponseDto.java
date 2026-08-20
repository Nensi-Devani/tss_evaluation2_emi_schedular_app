package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFinancialProfileResponseDto {

    private String pan;

    private String aadhar;

    private BigDecimal monthlyIncome;

    private BigDecimal existingMonthlyDebt;
}

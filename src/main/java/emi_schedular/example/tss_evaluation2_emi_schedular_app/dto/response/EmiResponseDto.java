package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmiResponseDto {

    private Long id;

    private Integer installmentNumber;

    private LocalDate dueDate;

    private BigDecimal principalAmount;

    private BigDecimal interestAmount;

    private BigDecimal emiAmount;

    private BigDecimal remainingBalance;

    private EmiStatus status;

    private LocalDate paidAt;
}

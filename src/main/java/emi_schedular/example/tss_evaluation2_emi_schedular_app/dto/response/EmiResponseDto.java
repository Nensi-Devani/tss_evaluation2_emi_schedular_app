package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmiResponseDto {

    private Long id;
    private Long loanId;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal emiAmount;
    private BigDecimal remainingBalance;
    private String status;
    private LocalDate paidAt;
}

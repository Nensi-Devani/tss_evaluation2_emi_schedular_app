package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanType;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.RiskLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {

    private Long id;

    private Long borrowerId;

    private String borrowerName;

    private String borrowerEmail;

    private Long approvedById;

    private String approvedByName;

    private LoanType loanType;

    private BigDecimal requestedAmount;

    private Integer requestedTenure;

    private BigDecimal monthlyIncome;

    private BigDecimal existingMonthlyDebt;

    private BigDecimal dti;

    private RiskLevel riskLevel;

    private LoanStrategy strategy;

    private BigDecimal interestRate;

    private BigDecimal emiAmount;

    private BigDecimal remainingDebtAmount;

    private LoanStatus loanStatus;

    private LocalDateTime approvedAt;

    private LocalDate firstEmiDate;
}

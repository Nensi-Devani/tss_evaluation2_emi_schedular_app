package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndividualLoanReportResponse {

    private Long loanId;

    private LoanStatus loanStatus;

    private LoanStrategy strategy;

    private BigDecimal requestedAmount;

    private BigDecimal interestRate;

    private Integer tenure;

    private BigDecimal emiAmount;

    private BigDecimal remainingDebtAmount;

    private long totalEmis;

    private long paidEmis;

    private long pendingEmis;

    private long overdueEmis;

    private BigDecimal totalPaidAmount;

    private List<EmiResponseDto> emis;
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanSummaryReportResponse {

    private long totalLoans;

    private long pendingLoans;

    private long activeLoans;

    private long rejectedLoans;

    private BigDecimal totalRequestedAmount;

    private BigDecimal totalOutstandingAmount;

    private long totalEmis;

    private long paidEmis;

    private long pendingEmis;

    private long overdueEmis;

    private BigDecimal totalCollectedAmount;

    private BigDecimal totalPenaltyCollected;
}

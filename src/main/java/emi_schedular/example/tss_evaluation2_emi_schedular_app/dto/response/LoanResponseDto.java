package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {

    private Long id;
    private String loanType;
    private BigDecimal requestedAmount;
    private Integer requestedTenure;
    private BigDecimal dti;
    private String riskLevel;
    private String strategy;
    private BigDecimal interestRate;
    private BigDecimal emiAmount;
    private BigDecimal outstandingAmount;
    private String loanStatus;
    private LocalDateTime approvedAt;
    private LocalDate firstEmiDate;
}

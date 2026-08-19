package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OverdueReportResponse {

    private Long loanId;

    private Long emiId;

    private Integer installmentNumber;

    private String borrowerName;

    private BigDecimal emiAmount;

    private BigDecimal penaltyAmount;

    private BigDecimal totalAmountDue;

    private LocalDate dueDate;

    private long overdueDays;
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanType;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.RiskLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoanFilterRequestDto {

    private LoanStatus status;

    private LoanType loanType;

    private RiskLevel riskLevel;
}

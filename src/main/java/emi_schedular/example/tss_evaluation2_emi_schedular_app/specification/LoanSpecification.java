package emi_schedular.example.tss_evaluation2_emi_schedular_app.specification;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanType;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.RiskLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor
public class LoanSpecification {

    public static Specification<Loan> hasLoanStatus(LoanStatus loanStatus) {
        return (root, query, criteriaBuilder) -> {
            if (loanStatus == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("loanStatus"),
                    loanStatus
            );
        };
    }

    public static Specification<Loan> hasLoanType(LoanType loanType) {
        return (root, query, criteriaBuilder) -> {
            if (loanType == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("loanType"),
                    loanType
            );
        };
    }

    public static Specification<Loan> hasRiskLevel(RiskLevel riskLevel) {
        return (root, query, criteriaBuilder) -> {
            if (riskLevel == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("riskLevel"),
                    riskLevel
            );
        };
    }
}
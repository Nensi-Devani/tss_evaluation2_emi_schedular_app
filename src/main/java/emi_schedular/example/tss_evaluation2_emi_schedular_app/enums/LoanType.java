package emi_schedular.example.tss_evaluation2_emi_schedular_app.enums;

import java.math.BigDecimal;

public enum LoanType {

    PERSONAL(new BigDecimal("14.00")),
    EDUCATION(new BigDecimal("9.50"));

    // default annual interest rate (%) applied to loans of this type
    private final BigDecimal interestRate;

    LoanType(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }
}

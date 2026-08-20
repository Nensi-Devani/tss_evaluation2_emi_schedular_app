package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.strategy;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlatRateEmiStrategy implements EmiCalculationStrategy {

    private static final int SCALE = 2;

    private static final BigDecimal PERSONAL_INTEREST_RATE = new BigDecimal("14.00");

    private static final BigDecimal EDUCATIONAL_INTEREST_RATE = new BigDecimal("9.50");

    @Override
    public LoanStrategy getStrategy() {
        return LoanStrategy.FLAT_RATE_LOAN;
    }

    @Override
    public List<Emi> generateSchedule(Loan loan) {
        validateLoan(loan);

        BigDecimal interestRate = getInterestRate(loan);

        loan.setInterestRate(interestRate);

        BigDecimal principal = loan.getRequestedAmount();

        int tenure = loan.getRequestedTenure();

        BigDecimal annualRate = interestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        BigDecimal years = BigDecimal.valueOf(tenure).divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal totalInterest = principal
                .multiply(annualRate)
                .multiply(years)
                .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal totalPayable = principal
                .add(totalInterest)
                .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal emiAmount = totalPayable.divide(BigDecimal.valueOf(tenure), SCALE, RoundingMode.HALF_UP);

        BigDecimal monthlyPrincipal = principal.divide(BigDecimal.valueOf(tenure), SCALE, RoundingMode.HALF_UP);

        BigDecimal monthlyInterest = totalInterest.divide(BigDecimal.valueOf(tenure), SCALE, RoundingMode.HALF_UP);

        List<Emi> emis = new ArrayList<>();

        BigDecimal remainingBalance = principal;

        LocalDate dueDate = loan.getFirstEmiDate();

        for (int i = 1; i <= tenure; i++) {
            BigDecimal principalAmount = monthlyPrincipal;

            BigDecimal interestAmount = monthlyInterest;

            BigDecimal currentEmi = emiAmount;

            if (i == tenure) {
                principalAmount = remainingBalance;

                currentEmi = principalAmount
                        .add(interestAmount)
                        .setScale(SCALE, RoundingMode.HALF_UP);
            }

            remainingBalance = remainingBalance
                    .subtract(principalAmount)
                    .max(BigDecimal.ZERO)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            Emi emi = new Emi();

            emi.setLoan(loan);
            emi.setInstallmentNumber(i);
            emi.setDueDate(dueDate);
            emi.setPrincipalAmount(principalAmount);
            emi.setInterestAmount(interestAmount);
            emi.setEmiAmount(currentEmi);
            emi.setRemainingBalance(remainingBalance);
            emi.setStatus(EmiStatus.PENDING);

            emis.add(emi);

            dueDate = dueDate.plusMonths(1);
        }

        return emis;
    }

    private BigDecimal getInterestRate(Loan loan) {
        return switch (loan.getLoanType()) {
            case PERSONAL -> PERSONAL_INTEREST_RATE;

            case EDUCATION -> EDUCATIONAL_INTEREST_RATE;

            default -> throw new UserApiException("Unsupported loan type: " + loan.getLoanType(), HttpStatus.BAD_REQUEST);
        };
    }

    private void validateLoan(Loan loan) {
        if (loan == null) {
            throw new UserApiException("Loan is required for EMI calculation", HttpStatus.BAD_REQUEST);
        }

        if (loan.getRequestedAmount() == null || loan.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserApiException("Loan amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }

        if (loan.getRequestedTenure() == null || loan.getRequestedTenure() <= 0) {
            throw new UserApiException("Loan tenure must be greater than zero", HttpStatus.BAD_REQUEST);
        }

        if (loan.getLoanType() == null) {
            throw new UserApiException("Loan type is required", HttpStatus.BAD_REQUEST);
        }

        if (loan.getFirstEmiDate() == null) {
            throw new UserApiException("First EMI date is required before loan approval", HttpStatus.BAD_REQUEST);
        }
    }
}
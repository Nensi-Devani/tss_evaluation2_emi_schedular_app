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
public class ReducingBalanceEmiStrategy implements EmiCalculationStrategy {

    private static final int SCALE = 2;
    private static final int CALCULATION_SCALE = 12;

    private static final BigDecimal PERSONAL_INTEREST_RATE = new BigDecimal("14.00");

    private static final BigDecimal EDUCATIONAL_INTEREST_RATE = new BigDecimal("9.50");

    @Override
    public LoanStrategy getStrategy() {
        return LoanStrategy.REDUCING_BALANCE_LOAN;
    }

    @Override
    public List<Emi> generateSchedule(Loan loan) {
        validateLoan(loan);

        BigDecimal interestRate = getInterestRate(loan);

        loan.setInterestRate(interestRate);

        BigDecimal principal = loan.getRequestedAmount();

        int tenure = loan.getRequestedTenure();

        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(1200), CALCULATION_SCALE, RoundingMode.HALF_UP);

        BigDecimal emiAmount;

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            emiAmount = principal.divide(BigDecimal.valueOf(tenure), SCALE, RoundingMode.HALF_UP);
        } else {
            BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);

            BigDecimal power = onePlusRate.pow(
                    tenure,
                    new java.math.MathContext(CALCULATION_SCALE, RoundingMode.HALF_UP)
            );

            BigDecimal numerator = principal
                    .multiply(monthlyRate)
                    .multiply(power);

            BigDecimal denominator = power.subtract(BigDecimal.ONE);

            emiAmount = numerator
                    .divide(denominator, CALCULATION_SCALE, RoundingMode.HALF_UP)
                    .setScale(SCALE, RoundingMode.HALF_UP);
        }

        List<Emi> emis = new ArrayList<>();

        BigDecimal remainingBalance = principal;

        LocalDate dueDate = loan.getFirstEmiDate();

        for (int i = 1; i <= tenure; i++) {
            BigDecimal interestAmount = remainingBalance
                    .multiply(monthlyRate)
                    .setScale(
                            SCALE,
                            RoundingMode.HALF_UP
                    );

            BigDecimal principalAmount;
            BigDecimal currentEmi;

            if (i == tenure) {
                principalAmount = remainingBalance;

                currentEmi = principalAmount
                        .add(interestAmount)
                        .setScale(SCALE, RoundingMode.HALF_UP);
            } else {
                currentEmi = emiAmount;

                principalAmount = currentEmi
                        .subtract(interestAmount)
                        .setScale(SCALE, RoundingMode.HALF_UP);

                if (principalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    principalAmount = BigDecimal.ZERO;
                }

                if (principalAmount.compareTo(remainingBalance) > 0) {
                    principalAmount = remainingBalance;

                    currentEmi = principalAmount
                            .add(interestAmount)
                            .setScale(SCALE, RoundingMode.HALF_UP);
                }
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
        if (loan.getLoanType() == null) {
            throw new UserApiException("Loan type is required to calculate interest rate", HttpStatus.BAD_REQUEST);
        }

        return switch (loan.getLoanType()) {
            case PERSONAL -> PERSONAL_INTEREST_RATE;

            case EDUCATION -> EDUCATIONAL_INTEREST_RATE;

            default -> throw new UserApiException("Unsupported loan type: " + loan.getLoanType(), HttpStatus.BAD_REQUEST);
        };
    }

    private void validateLoan(Loan loan) {
        if (loan == null) {
            throw new UserApiException("Loan is required", HttpStatus.BAD_REQUEST);
        }

        if (loan.getRequestedAmount() == null || loan.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserApiException("Loan amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }

        if (loan.getRequestedTenure() == null || loan.getRequestedTenure() <= 0) {
            throw new UserApiException("Loan tenure must be greater than zero", HttpStatus.BAD_REQUEST);
        }

        if (loan.getFirstEmiDate() == null) {
            throw new UserApiException("First EMI date is required before loan approval", HttpStatus.BAD_REQUEST);
        }

        if (loan.getLoanType() == null) {
            throw new UserApiException("Loan type is required", HttpStatus.BAD_REQUEST);
        }
    }
}
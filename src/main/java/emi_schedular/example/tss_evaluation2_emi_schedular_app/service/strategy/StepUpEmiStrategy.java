package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.strategy;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class StepUpEmiStrategy implements EmiCalculationStrategy {

    private static final int SCALE = 2;
    private static final int CALCULATION_SCALE = 12;

    // emi increases by 5% after every 12 months.
    private static final BigDecimal ANNUAL_STEP_UP = new BigDecimal("0.05");

    private static final BigDecimal PERSONAL_INTEREST_RATE = new BigDecimal("14.00");

    private static final BigDecimal EDUCATIONAL_INTEREST_RATE = new BigDecimal("9.50");

    @Override
    public LoanStrategy getStrategy() {
        return LoanStrategy.STEP_UP_EMI_LOAN;
    }

    @Override
    public List<Emi> generateSchedule(Loan loan) {
        validateLoan(loan);

        BigDecimal interestRate = getInterestRate(loan);

        loan.setInterestRate(interestRate);

        BigDecimal principal = loan.getRequestedAmount();

        int tenure = loan.getRequestedTenure();

        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(1200), CALCULATION_SCALE, RoundingMode.HALF_UP);

        BigDecimal baseEmi = calculateBaseEmi(principal, monthlyRate, tenure);

        List<Emi> emis = new ArrayList<>();

        BigDecimal remainingBalance = principal;

        LocalDate dueDate = loan.getFirstEmiDate();

        for (int i = 1; i <= tenure; i++) {
            int yearNumber = (i - 1) / 12;

            BigDecimal stepMultiplier = calculateStepMultiplier(yearNumber);

            BigDecimal currentEmi = baseEmi
                    .multiply(stepMultiplier)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            BigDecimal interestAmount = remainingBalance
                    .multiply(monthlyRate)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            BigDecimal principalAmount;

            if (i == tenure) {
                principalAmount = remainingBalance;

                currentEmi = principalAmount
                        .add(interestAmount)
                        .setScale(SCALE, RoundingMode.HALF_UP);
            } else {
                principalAmount = currentEmi
                        .subtract(interestAmount)
                        .setScale(SCALE, RoundingMode.HALF_UP);

                if (principalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new UserApiException("Step-up EMI configuration does not amortize the loan",    HttpStatus.BAD_REQUEST);
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

    private BigDecimal calculateBaseEmi(BigDecimal principal, BigDecimal monthlyRate, int tenure) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal presentValueFactor = BigDecimal.ZERO;

            for (int month = 1; month <= tenure; month++) {
                int yearNumber = (month - 1) / 12;

                BigDecimal stepMultiplier = calculateStepMultiplier(yearNumber);

                presentValueFactor = presentValueFactor.add(stepMultiplier);
            }

            return principal.divide(presentValueFactor, SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal presentValueFactor = BigDecimal.ZERO;

        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);

        for (int month = 1; month <= tenure; month++) {
            int yearNumber = (month - 1) / 12;

            BigDecimal stepMultiplier = calculateStepMultiplier(yearNumber);

            BigDecimal power = onePlusRate.pow(
                    month,
                    new MathContext(CALCULATION_SCALE, RoundingMode.HALF_UP)
            );

            BigDecimal discountFactor = BigDecimal.ONE.divide(power, CALCULATION_SCALE, RoundingMode.HALF_UP);

            BigDecimal presentValue = stepMultiplier.multiply(discountFactor);

            presentValueFactor = presentValueFactor.add(presentValue);
        }

        return principal.divide(presentValueFactor, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateStepMultiplier(int yearNumber) {
        return BigDecimal.ONE
                .add(ANNUAL_STEP_UP)
                .pow(yearNumber);
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
            throw new UserApiException("Loan is required", HttpStatus.BAD_REQUEST);
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
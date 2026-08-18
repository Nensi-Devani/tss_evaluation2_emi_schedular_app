package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanType;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.EmiCalculationService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.strategy.EmiCalculationStrategy;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.strategy.EmiStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmiCalculationServiceImpl implements EmiCalculationService {

    private static final BigDecimal PERSONAL_INTEREST_RATE = new BigDecimal("14.00");
    private static final BigDecimal EDUCATIONAL_INTEREST_RATE = new BigDecimal("9.50");

    private final EmiStrategyFactory emiStrategyFactory;

    @Override
    public List<Emi> generateSchedule(Loan loan) {
        if (loan == null) {
            throw new UserApiException("Loan is required for EMI calculation", HttpStatus.BAD_REQUEST);
        }

        if (loan.getLoanType() == null) {
            throw new UserApiException("Loan type is required for EMI calculation", HttpStatus.BAD_REQUEST);
        }

        BigDecimal interestRate;

        if (loan.getLoanType() == LoanType.PERSONAL) {
            interestRate = PERSONAL_INTEREST_RATE;
        } else if (loan.getLoanType() == LoanType.EDUCATION) {
            interestRate = EDUCATIONAL_INTEREST_RATE;
        } else {
            throw new UserApiException("Unsupported loan type: " + loan.getLoanType(), HttpStatus.BAD_REQUEST);
        }

        loan.setInterestRate(interestRate);

        log.info(
                "Generating EMI schedule. loanId={}, loanType={}, interestRate={}, strategy={}",
                loan.getId(),
                loan.getLoanType(),
                loan.getInterestRate(),
                loan.getStrategy()
        );

        if (loan.getStrategy() == null) {
            throw new UserApiException("Loan strategy is required for EMI calculation", HttpStatus.BAD_REQUEST);
        }

        EmiCalculationStrategy strategy = emiStrategyFactory.getStrategy(loan.getStrategy());

        List<Emi> emis = strategy.generateSchedule(loan);

        log.info(
                "EMI schedule generated. loanId={}, loanType={}, interestRate={}, strategy={}, emiCount={}",
                loan.getId(),
                loan.getLoanType(),
                loan.getInterestRate(),
                loan.getStrategy(),
                emis.size()
        );

        return emis;
    }
}
package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.strategy;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class EmiStrategyFactory {

    private final Map<LoanStrategy, EmiCalculationStrategy> strategies;

    public EmiStrategyFactory(List<EmiCalculationStrategy> strategyList) {
        this.strategies = new EnumMap<>(LoanStrategy.class);

        for (EmiCalculationStrategy strategy : strategyList) {
            strategies.put(strategy.getStrategy(), strategy);
        }
    }

    public EmiCalculationStrategy getStrategy(LoanStrategy loanStrategy) {
        EmiCalculationStrategy strategy = strategies.get(loanStrategy);

        if (strategy == null) {
            throw new UserApiException("No EMI calculation strategy configured for: " + loanStrategy, HttpStatus.BAD_REQUEST);
        }

        return strategy;
    }
}
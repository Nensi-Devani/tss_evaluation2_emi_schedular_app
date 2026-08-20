package emi_schedular.example.tss_evaluation2_emi_schedular_app.strategy;


import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.RiskLevel;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@RequiredArgsConstructor
public class StrategyDecisionEngine {

    private final ConfigService configService;

    public StrategyDecision decide(BigDecimal dti, int tenure) {

        BigDecimal d1 = configService.getDecimal("DTI_LOW_THRESHOLD");
        BigDecimal d2 = configService.getDecimal("DTI_HIGH_THRESHOLD");
        int thresholdMonth = configService.getInt("STRATEGY_TENURE_THRESHOLD_MONTHS");

        if (dti.compareTo(d1) < 0) {
            return new StrategyDecision(RiskLevel.LOW, LoanStrategy.FLAT_RATE_LOAN);
        }

        if (dti.compareTo(d2) <= 0) {
            LoanStrategy suggested = tenure < thresholdMonth ? LoanStrategy.REDUCING_BALANCE_LOAN : LoanStrategy.STEP_UP_EMI_LOAN;
            return new StrategyDecision(RiskLevel.MEDIUM, suggested);
        }

        // above D2 -> high risk, flagged for rejection, no strategy suggested
        return new StrategyDecision(RiskLevel.HIGH, null);
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.strategy;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.RiskLevel;


public record StrategyDecision(RiskLevel riskLevel, LoanStrategy suggestedStrategy) {
}

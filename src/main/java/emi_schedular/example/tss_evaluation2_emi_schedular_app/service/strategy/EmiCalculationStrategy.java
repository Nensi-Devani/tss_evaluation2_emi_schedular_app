package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.strategy;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;

import java.util.List;

public interface EmiCalculationStrategy {

    LoanStrategy getStrategy();

    List<Emi> generateSchedule(Loan loan);
}

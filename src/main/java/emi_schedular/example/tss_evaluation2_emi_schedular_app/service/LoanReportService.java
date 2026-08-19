package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.IndividualLoanReportResponse;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanSummaryReportResponse;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.OverdueReportResponse;

import java.util.List;

public interface LoanReportService {

    LoanSummaryReportResponse getLoanSummaryReport();

    List<OverdueReportResponse> getOverdueLoanReport();

    IndividualLoanReportResponse getIndividualLoanReport(Long loanId);
}

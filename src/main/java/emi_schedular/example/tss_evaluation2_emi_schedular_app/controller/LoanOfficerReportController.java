package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.IndividualLoanReportResponse;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanSummaryReportResponse;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.OverdueReportResponse;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.LoanReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loan-officer/reports")
@RequiredArgsConstructor
public class LoanOfficerReportController {

    private final LoanReportService loanReportService;

    @GetMapping("/loans/summary")
    public ResponseEntity<LoanSummaryReportResponse> getLoanSummaryReport() {
        return ResponseEntity.ok(loanReportService.getLoanSummaryReport());
    }

    @GetMapping("/loans/overdue")
    public ResponseEntity<List<OverdueReportResponse>> getOverdueLoanReport() {
        return ResponseEntity.ok(loanReportService.getOverdueLoanReport());
    }

    @GetMapping("/loans/{loanId}")
    public ResponseEntity<IndividualLoanReportResponse> getIndividualLoanReport(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanReportService.getIndividualLoanReport(loanId));
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.LoanFilterRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateLoanStrategyRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.*;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.LoanOfficerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/loan-officer/loans")
@RequiredArgsConstructor
@Slf4j
public class LoanOfficerController {

    private final LoanOfficerService loanOfficerService;

    @GetMapping
    public ResponseEntity<PageDto<LoanResponseDto>> getAllLoans(LoanFilterRequestDto filter, @PageableDefault(page = 0,size = 10,sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(loanOfficerService.getAllLoans(filter,pageable));
    }

    @GetMapping("/pending")
    public ResponseEntity<PageDto<LoanResponseDto>> getPendingLoans(@PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(loanOfficerService.getPendingLoans(pageable));
    }

    @GetMapping("/{id}/borrower")
    public ResponseEntity<BorrowerResponseDto> getBorrowerByLoanId(@PathVariable Long id) {
        return ResponseEntity.ok(loanOfficerService.getBorrowerByLoanId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable Long id) {
       return ResponseEntity.ok(loanOfficerService.getLoanById(id));
    }

    @GetMapping("/{id}/financial-profile")
    public ResponseEntity<UserFinancialProfileResponseDto> getFinancialProfileByLoanId(@PathVariable Long id) {
        return ResponseEntity.ok(loanOfficerService.getFinancialProfileByLoanId(id));
    }

    @GetMapping("/{id}/emis")
    public ResponseEntity<PageDto<EmiResponseDto>> getEmisByLoanId(@PathVariable Long id, @PageableDefault(page = 0, size = 10, sort = "installmentNumber") Pageable pageable) {
        return ResponseEntity.ok(loanOfficerService.getEmisByLoanId(id, pageable));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<PageDto<PaymentResponseDto>> getPaymentsByLoanId(@PathVariable Long id, @PageableDefault(page = 0, size = 10, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(loanOfficerService.getPaymentsByLoanId(id,pageable));
    }

    @GetMapping("/emis/overdue")
    public ResponseEntity<PageDto<EmiResponseDto>> getOverdueEmis(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month, Pageable pageable) {
        return ResponseEntity.ok(loanOfficerService.getOverdueEmis(month, pageable));
    }

    @GetMapping("/{loanId}/emis/{emiId}")
    public ResponseEntity<EmiResponseDto> getEmiByLoanAndEmiId(@PathVariable Long loanId, @PathVariable Long emiId) {
        return ResponseEntity.ok(loanOfficerService.getEmiByLoanAndEmiId(loanId, emiId));
    }

    @GetMapping("/{loanId}/strategy")
    public ResponseEntity<LoanStrategyResponseDto> getLoanStrategy(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanOfficerService.getLoanStrategy(loanId));
    }

    @PatchMapping("/{loanId}/strategy")
    public ResponseEntity<LoanStrategyResponseDto> updateLoanStrategy(@PathVariable Long loanId, @Valid @RequestBody UpdateLoanStrategyRequestDto request) {
        return ResponseEntity.ok(loanOfficerService.updateLoanStrategy(loanId, request));
    }

    @PostMapping("/{loanId}/approve")
    public ResponseEntity<Void> approveLoan(@PathVariable Long loanId) {
        loanOfficerService.approveLoan(loanId);
        return ResponseEntity.ok().build();
    }
}

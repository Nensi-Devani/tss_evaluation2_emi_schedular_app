package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.LoanFilterRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.*;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.LoanOfficerService;
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
        PageDto<LoanResponseDto> response = loanOfficerService.getAllLoans(filter,pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<PageDto<LoanResponseDto>> getPendingLoans(@PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable) {
        PageDto<LoanResponseDto> response = loanOfficerService.getPendingLoans(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/borrower")
    public ResponseEntity<BorrowerResponseDto> getBorrowerByLoanId(@PathVariable Long id) {
        BorrowerResponseDto response = loanOfficerService.getBorrowerByLoanId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable Long id) {
        LoanResponseDto response = loanOfficerService.getLoanById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/financial-profile")
    public ResponseEntity<UserFinancialProfileResponseDto> getFinancialProfileByLoanId(@PathVariable Long id) {
        UserFinancialProfileResponseDto response = loanOfficerService.getFinancialProfileByLoanId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/emis")
    public ResponseEntity<PageDto<EmiResponseDto>> getEmisByLoanId(@PathVariable Long id, @PageableDefault(page = 0, size = 10, sort = "installmentNumber") Pageable pageable) {
        PageDto<EmiResponseDto> response = loanOfficerService.getEmisByLoanId(id, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<PageDto<PaymentResponseDto>> getPaymentsByLoanId(@PathVariable Long id, @PageableDefault(page = 0, size = 10, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        PageDto<PaymentResponseDto> response = loanOfficerService.getPaymentsByLoanId(id,pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/emis/overdue")
    public ResponseEntity<PageDto<EmiResponseDto>> getOverdueEmis(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month, Pageable pageable) {
        PageDto<EmiResponseDto> response = loanOfficerService.getOverdueEmis(month, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{loanId}/emis/{emiId}")
    public ResponseEntity<EmiResponseDto> getEmiByLoanAndEmiId(@PathVariable Long loanId, @PathVariable Long emiId) {
        EmiResponseDto response = loanOfficerService.getEmiByLoanAndEmiId(loanId, emiId);

        return ResponseEntity.ok(response);
    }
}

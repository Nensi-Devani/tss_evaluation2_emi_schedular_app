package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.PaymentResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.BorrowerPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrower/loans")
@RequiredArgsConstructor
public class BorrowerPaymentController {

    private final BorrowerPaymentService borrowerPaymentService;

    @GetMapping("/{loanId}/payments")
    public ResponseEntity<PageDto<PaymentResponseDto>> getPaymentHistory(@PathVariable Long loanId, Pageable pageable) {
        return ResponseEntity.ok(borrowerPaymentService.getPaymentHistory(loanId, pageable));
    }
}

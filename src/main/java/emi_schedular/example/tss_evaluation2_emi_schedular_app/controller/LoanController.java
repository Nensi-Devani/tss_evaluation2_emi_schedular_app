package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.LoanApplicationRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanTypeResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrower/loans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BORROWER')")
public class LoanController {

    private final LoanService loanService;


    // POST /api/borrower/loans/apply
    @PostMapping("/apply")
    public ResponseEntity<String> applyLoan(@Valid @RequestBody LoanApplicationRequestDto request,
                                            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.applyLoan(request, email));
    }

    // GET /api/borrower/loans/types
    @GetMapping("/types")
    public ResponseEntity<List<LoanTypeResponseDto>> getLoanTypes() {
        return ResponseEntity.ok(loanService.getAllLoanTypes());
    }

    // GET /api/borrower/loans
    @GetMapping
    public ResponseEntity<List<LoanResponseDto>> getMyLoans(Authentication authentication) {
        return ResponseEntity.ok(loanService.getMyLoans(authentication.getName()));
    }

    // GET /api/borrower/loans/{loanId}
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponseDto> getMyLoanById(@PathVariable Long loanId, Authentication authentication) {
        return ResponseEntity.ok(loanService.getMyLoanById(authentication.getName(), loanId));
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.LoanApplicationRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanTypeResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface LoanService {

    String applyLoan(@Valid LoanApplicationRequestDto request, String email);

    List<LoanTypeResponseDto> getAllLoanTypes();

    List<LoanResponseDto> getMyLoans(String email);

    LoanResponseDto getMyLoanById(String email, Long loanId);
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.LoanFilterRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateLoanStrategyRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;

public interface LoanOfficerService {

    PageDto<LoanResponseDto> getAllLoans(LoanFilterRequestDto filter, Pageable pageable);

    PageDto<LoanResponseDto> getPendingLoans(Pageable pageable);

    BorrowerResponseDto getBorrowerByLoanId(Long loanId);

    LoanResponseDto getLoanById(Long loanId);

    UserFinancialProfileResponseDto getFinancialProfileByLoanId(Long loanId);

    PageDto<EmiResponseDto> getEmisByLoanId(Long loanId, Pageable pageable);

    PageDto<PaymentResponseDto> getPaymentsByLoanId(Long loanId, Pageable pageable);

    PageDto<EmiResponseDto> getOverdueEmis(YearMonth month, Pageable pageable);

    EmiResponseDto getEmiByLoanAndEmiId(Long loanId, Long emiId);

    LoanStrategyResponseDto getLoanStrategy(Long loanId);

    LoanStrategyResponseDto updateLoanStrategy(Long loanId,UpdateLoanStrategyRequestDto request);

    void approveLoan(Long loanId);

    void rejectLoan(Long loanId);
}
package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.PaymentResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Payment;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper.PaymentMapper;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.LoanRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.PaymentRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.BorrowerPaymentService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowerPaymentServiceImpl implements BorrowerPaymentService {

    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final SecurityService securityService;

    @Override
    @Transactional(readOnly = true)
    public PageDto<PaymentResponseDto> getPaymentHistory(Long loanId, Pageable pageable) {
        User currentUser = securityService.getCurrentUser();

        boolean ownsLoan = loanRepository.existsByIdAndBorrowerId(
                loanId,
                currentUser.getId()
        );

        if (!ownsLoan) {
            throw new ResourceNotFoundException("Loan not found");
        }

        Page<Payment> paymentPage = paymentRepository.findPaymentHistoryByLoanId(loanId, pageable);

        return new PageDto<>(
                paymentPage,
                paymentMapper::toResponseDto
        );
    }
}

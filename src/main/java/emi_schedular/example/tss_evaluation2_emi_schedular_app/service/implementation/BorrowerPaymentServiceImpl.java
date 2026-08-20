package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.PaymentResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Payment;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.AuditAction;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.PaymentStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper.PaymentMapper;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.EmiRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.LoanRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.PaymentRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AuditLogService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.BorrowerPaymentService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowerPaymentServiceImpl implements BorrowerPaymentService {

    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;
    private final EmiRepository emiRepository;

    private final PaymentMapper paymentMapper;
    private final SecurityService securityService;
    private final AuditLogService auditLogService;

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

    @Override
    @Transactional
    public void payEmi(Long emiId) {
        log.info("EMI payment requested. emiId={}", emiId);

        Emi emi = emiRepository.findById(emiId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("EMI not found with id: " + emiId)
                );

        Loan loan = emi.getLoan();

        if (loan == null) {
            throw new UserApiException("Loan not found for EMI", HttpStatus.BAD_REQUEST);
        }

        User currentUser = securityService.getCurrentUser();

        if (loan.getBorrower() == null || !loan.getBorrower().getId().equals(currentUser.getId())) {
            throw new UserApiException("You are not authorized to pay this EMI", HttpStatus.FORBIDDEN);
        }

        if (emi.getStatus() == EmiStatus.PAID || emi.getStatus() == EmiStatus.OVERDUE) {
            throw new UserApiException("This EMI has already been paid", HttpStatus.BAD_REQUEST);
        }

        Emi firstUnpaidEmi = emiRepository
                .findFirstByLoanIdAndStatusInOrderByInstallmentNumberAsc(
                        loan.getId(),
                        List.of(EmiStatus.PENDING)
                )
                .orElseThrow(() ->
                        new UserApiException("There are no pending EMIs to pay",HttpStatus.BAD_REQUEST)
                );

        if (!firstUnpaidEmi.getId().equals(emi.getId())) {
            throw new UserApiException(
                    "Please complete the payment for EMI no. " + firstUnpaidEmi.getInstallmentNumber() + " before making the payment for EMI no. " + emi.getInstallmentNumber(),
                    HttpStatus.BAD_REQUEST
            );
        }

        LocalDate today = LocalDate.now();

        BigDecimal penalty = BigDecimal.ZERO;

        boolean overdue = emi.getDueDate() != null && emi.getDueDate().isBefore(today);

        if (overdue) {
            emi.setStatus(EmiStatus.OVERDUE);

            penalty = new BigDecimal("100.00");

            auditLogService.createAuditLog(
                    currentUser,
                    AuditAction.EMI_MARKED_OVERDUE,
                    "EMI",
                    emiId
            );

            log.info(
                    "EMI marked overdue. emiId={}, dueDate={}, penalty={}",
                    emiId,
                    emi.getDueDate(),
                    penalty
            );
        }

        BigDecimal totalAmount = emi.getEmiAmount()
                .add(penalty)
                .setScale(2, RoundingMode.HALF_UP);

        Payment payment = new Payment();

        payment.setEmi(emi);
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        if (!overdue) {
            emi.setStatus(EmiStatus.PAID);

            auditLogService.createAuditLog(
                    currentUser,
                    AuditAction.EMI_MARKED_PAID,
                    "EMI",
                    emiId
            );
        }

        emi.setPaidAt(today);

        emiRepository.save(emi);

        BigDecimal remainingDebt = loan.getRemainingDebtAmount();

        if (remainingDebt == null) {
            remainingDebt = loan.getRequestedAmount();
        }

        remainingDebt = remainingDebt
                .subtract(emi.getEmiAmount())
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        loan.setRemainingDebtAmount(remainingDebt);

        loanRepository.save(loan);

        log.info(
                "EMI payment successful. emiId={}, loanId={}, emiAmount={}, penalty={}, totalAmount={}, finalEmiStatus={}, remainingDebt={}",
                emiId,
                loan.getId(),
                emi.getEmiAmount(),
                penalty,
                totalAmount,
                emi.getStatus(),
                remainingDebt
        );
    }
}

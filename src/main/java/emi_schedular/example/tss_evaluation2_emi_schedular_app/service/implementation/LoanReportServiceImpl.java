package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.EmiResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.IndividualLoanReportResponse;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanSummaryReportResponse;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.OverdueReportResponse;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Payment;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.EmiRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.LoanRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.PaymentRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.LoanReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LoanReportServiceImpl implements LoanReportService {

    private static final BigDecimal OVERDUE_PENALTY = new BigDecimal("100.00");

    private final LoanRepository loanRepository;
    private final EmiRepository emiRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public LoanSummaryReportResponse getLoanSummaryReport() {
        List<Loan> loans = loanRepository.findAll();
        List<Emi> emis = emiRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();

        long pendingLoans = loans.stream()
                .filter(loan -> loan.getLoanStatus() != null && loan.getLoanStatus().name().equals("PENDING"))
                .count();

        long activeLoans = loans.stream()
                .filter(loan -> loan.getLoanStatus() != null && loan.getLoanStatus().name().equals("ACTIVE"))
                .count();

        long rejectedLoans = loans.stream()
                .filter(loan -> loan.getLoanStatus() != null && loan.getLoanStatus().name().equals("REJECTED"))
                .count();

        long paidEmis = emis.stream()
                .filter(emi -> emi.getStatus() == EmiStatus.PAID)
                .count();

        long pendingEmis = emis.stream()
                .filter(emi -> emi.getStatus() == EmiStatus.PENDING)
                .count();

        long overdueEmis = emis.stream()
                .filter(emi -> emi.getStatus() == EmiStatus.OVERDUE)
                .count();

        BigDecimal totalRequestedAmount = loans.stream()
                .map(Loan::getRequestedAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalOutstandingAmount = loans.stream()
                .map(Loan::getRemainingDebtAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalCollectedAmount = payments.stream()
                .filter(payment -> payment.getAmount() != null)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPenaltyCollected = emis.stream()
                .filter(emi -> emi.getStatus() == EmiStatus.OVERDUE)
                .filter(emi -> emi.getDueDate() != null)
                .filter(emi -> emi.getPaidAt() != null)
                .filter(emi -> emi.getPaidAt().isAfter(emi.getDueDate()))
                .map(emi -> OVERDUE_PENALTY)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new LoanSummaryReportResponse(
                loans.size(),
                pendingLoans,
                activeLoans,
                rejectedLoans,
                totalRequestedAmount,
                totalOutstandingAmount,
                emis.size(),
                paidEmis,
                pendingEmis,
                overdueEmis,
                totalCollectedAmount,
                totalPenaltyCollected
        );
    }

    @Override
    public List<OverdueReportResponse> getOverdueLoanReport() {
        LocalDate today = LocalDate.now();

        List<Emi> overdueEmis = emiRepository.findAll()
                .stream()
                .filter(emi ->
                        emi.getStatus() == EmiStatus.OVERDUE || (emi.getStatus() == EmiStatus.PENDING && emi.getDueDate() != null && emi.getDueDate().isBefore(today))
                )
                .sorted(Comparator.comparing(Emi::getDueDate))
                .toList();

        List<OverdueReportResponse> reports = new ArrayList<>();

        for (Emi emi : overdueEmis) {
            Loan loan = emi.getLoan();

            String borrowerName = null;

            if (loan != null && loan.getBorrower() != null) {
                borrowerName = loan.getBorrower().getFullName();
            }

            long overdueDays = ChronoUnit.DAYS.between(emi.getDueDate(), today);

            if (overdueDays < 0) {
                overdueDays = 0;
            }

            BigDecimal penaltyAmount = OVERDUE_PENALTY;

            BigDecimal totalAmountDue = emi.getEmiAmount()
                    .add(penaltyAmount)
                    .setScale(2, RoundingMode.HALF_UP);

            reports.add(
                    new OverdueReportResponse(
                            loan != null ? loan.getId() : null,
                            emi.getId(),
                            emi.getInstallmentNumber(),
                            borrowerName,
                            emi.getEmiAmount(),
                            penaltyAmount,
                            totalAmountDue,
                            emi.getDueDate(),
                            overdueDays
                    )
            );
        }

        return reports;
    }

    @Override
    public IndividualLoanReportResponse getIndividualLoanReport(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Loan not found with id: " + loanId)
                );

        List<Emi> emis = emiRepository.findAll()
                .stream()
                .filter(emi -> emi.getLoan() != null && emi.getLoan().getId().equals(loanId))
                .sorted(Comparator.comparing(Emi::getInstallmentNumber))
                .toList();

        List<Payment> payments = paymentRepository.findAll()
                .stream()
                .filter(payment ->
                        payment.getEmi() != null && payment.getEmi().getLoan() != null && payment.getEmi().getLoan().getId().equals(loanId)
                )
                .toList();

        long paidEmis = emis.stream()
                .filter(emi -> emi.getStatus() == EmiStatus.PAID)
                .count();

        long pendingEmis = emis.stream()
                .filter(emi -> emi.getStatus() == EmiStatus.PENDING)
                .count();

        long overdueEmis = emis.stream()
                .filter(emi -> emi.getStatus() == EmiStatus.OVERDUE)
                .count();

        BigDecimal totalPaidAmount = payments.stream()
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        List<EmiResponseDto> emiResponses =
                emis.stream()
                        .map(emi -> createEmiResponse(emi, payments))
                        .toList();

        return new IndividualLoanReportResponse(
                loan.getId(),
                loan.getLoanStatus(),
                loan.getStrategy(),
                loan.getRequestedAmount(),
                loan.getInterestRate(),
                loan.getRequestedTenure(),
                loan.getEmiAmount(),
                loan.getRemainingDebtAmount(),
                emis.size(),
                paidEmis,
                pendingEmis,
                overdueEmis,
                totalPaidAmount,
                emiResponses
        );
    }

    private EmiResponseDto createEmiResponse(Emi emi, List<Payment> payments) {
        Payment payment = payments.stream()
                .filter(p ->
                        p.getEmi() != null && p.getEmi().getId().equals(emi.getId())
                )
                .findFirst()
                .orElse(null);

        BigDecimal paymentAmount = payment != null ? payment.getAmount() : BigDecimal.ZERO;

        BigDecimal penaltyAmount = BigDecimal.ZERO;

        if (payment != null && emi.getDueDate() != null && emi.getPaidAt() != null && emi.getPaidAt().isAfter(emi.getDueDate())) {
            penaltyAmount = new BigDecimal("100.00");
        }

        EmiResponseDto response = new EmiResponseDto();

        response.setId(emi.getId());
        response.setInstallmentNumber(emi.getInstallmentNumber());
        response.setDueDate(emi.getDueDate());
        response.setPrincipalAmount(emi.getPrincipalAmount());
        response.setInterestAmount(emi.getInterestAmount());
        response.setEmiAmount(emi.getEmiAmount());
        response.setRemainingBalance(emi.getRemainingBalance());
        response.setStatus(emi.getStatus());
        response.setPaidAt(emi.getPaidAt());
        response.setPrincipalAmount(paymentAmount);
//        response.setPenaltyAmount(penaltyAmount);

        return response;
    }
}

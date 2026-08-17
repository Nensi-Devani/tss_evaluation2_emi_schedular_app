package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.LoanFilterRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateLoanStrategyRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.*;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.*;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.*;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper.*;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.EmiRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.LoanRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.PaymentRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AuditLogService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.LoanOfficerService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.SecurityService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.specification.LoanSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LoanOfficerServiceImpl implements LoanOfficerService {

    private final LoanRepository loanRepository;
    private final EmiRepository emiRepository;
    private final PaymentRepository paymentRepository;

    private final LoanMapper loanMapper;
    private final EmiMapper emiMapper;
    private final PaymentMapper paymentMapper;
    private final UserMapper userMapper;
    private final UserFinancialProfileMapper userFinancialProfileMapper;

    private final AuditLogService auditLogService;
    private final SecurityService securityService;

    @Override
    public PageDto<LoanResponseDto> getAllLoans(LoanFilterRequestDto filter, Pageable pageable) {
        log.info(
                "Fetching loans. status={}, loanType={}, riskLevel={}, page={}, size={}",
                filter.getStatus(),
                filter.getLoanType(),
                filter.getRiskLevel(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        var specification = LoanSpecification.hasLoanStatus(filter.getStatus())
                .and(LoanSpecification.hasLoanType(filter.getLoanType()))
                .and(LoanSpecification.hasRiskLevel(filter.getRiskLevel()));

        Page<Loan> loanPage = loanRepository.findAll(specification, pageable);

        return new PageDto<>(
                loanPage,
                loanMapper::toResponseDto
        );
    }

    @Override
    public PageDto<LoanResponseDto> getPendingLoans(Pageable pageable) {
        log.info(
                "Fetching pending loans. page={}, size={}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Loan> loanPage = loanRepository.findAll(LoanSpecification.hasLoanStatus(LoanStatus.PENDING), pageable);

        return new PageDto<>(
                loanPage,
                loanMapper::toResponseDto
        );
    }

    @Override
    public BorrowerResponseDto getBorrowerByLoanId(Long loanId) {
        Loan loan =  getLoanEntity(loanId);

        User borrower = loan.getBorrower();

        return userMapper.toBorrowerResponseDto(borrower);
    }

    @Override
    public LoanResponseDto getLoanById(Long loanId) {
        log.info("Fetching loan by id={}", loanId);

        Loan loan =  getLoanEntity(loanId);

        return loanMapper.toResponseDto(loan);
    }

    @Override
    public UserFinancialProfileResponseDto getFinancialProfileByLoanId(Long loanId) {
        log.info(
                "Fetching financial profile for loan id: {}",
                loanId
        );

        Loan loan =  getLoanEntity(loanId);

        User borrower = loan.getBorrower();

        UserFinancialProfile userFinancialProfile = borrower.getFinancialProfile();

        return userFinancialProfileMapper.toResponseDto(userFinancialProfile);
    }

    @Override
    public PageDto<EmiResponseDto> getEmisByLoanId(Long loanId, Pageable pageable) {
        log.info(
                "Fetching EMI schedule for loan id={}, page={}, size={}",
                loanId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        // Verify that the loan exists.
        getLoanEntity(loanId);

        Page<Emi> emiPage = emiRepository.findByLoanId(loanId, pageable);

        return new PageDto<>(
                emiPage,
                emiMapper::toResponseDto
        );
    }

    @Override
    public PageDto<PaymentResponseDto> getPaymentsByLoanId(Long loanId, Pageable pageable) {
        log.info(
                "Fetching payment history for loan id={}, page={}, size={}",
                loanId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        // Verify that the loan exists.
        getLoanEntity(loanId);

        Page<Payment> paymentPage = paymentRepository.findByEmiLoanId(loanId, pageable);

        return new PageDto<>(
                paymentPage,
                paymentMapper::toResponseDto
        );
    }

    @Override
    public PageDto<EmiResponseDto> getOverdueEmis(YearMonth month, Pageable pageable) {
        log.info(
                "Fetching overdue EMIs. month={}, page={}, size={}",
                month,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Emi> emiPage;

        if (month == null) {
            emiPage = emiRepository.findOverdueEmis(
                    EmiStatus.OVERDUE,
                    LoanStatus.ACTIVE,
                    pageable
            );
        } else {
            emiPage = emiRepository.findOverdueEmisByMonth(
                    EmiStatus.OVERDUE,
                    LoanStatus.ACTIVE,
                    month.atDay(1),
                    month.atEndOfMonth(),
                    pageable
            );
        }

        return new PageDto<>(
                emiPage,
                emiMapper::toResponseDto
        );
    }

    @Override
    public EmiResponseDto getEmiByLoanAndEmiId(Long loanId, Long emiId) {
        log.info(
                "Fetching EMI for loan. loanId={}, emiId={}",
                loanId,
                emiId
        );

        Emi emi = emiRepository.findByIdAndLoanId(emiId, loanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "EMI not found for loanId=" + loanId
                                        + " and emiId=" + emiId
                        )
                );

        return emiMapper.toResponseDto(emi);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanStrategyResponseDto getLoanStrategy(Long loanId) {
        log.info(
                "Loan officer requested loan strategy. loanId={}",
                loanId
        );

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Loan not found with id: " + loanId
                        )
                );

        return new LoanStrategyResponseDto(
                loan.getId(),
                loan.getStrategy()
        );
    }

    @Override
    @Transactional
    public LoanStrategyResponseDto updateLoanStrategy(Long loanId, UpdateLoanStrategyRequestDto request) {
        log.info(
                "Loan officer requested strategy update. loanId={}, strategy={}",
                loanId,
                request.getStrategy()
        );

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Loan not found with id: " + loanId
                        )
                );

        if (loan.getLoanStatus() != LoanStatus.PENDING) {
            throw new UserApiException("Strategy can only be changed while the loan is pending", HttpStatus.BAD_REQUEST);
        }

        LoanStrategy oldStrategy = loan.getStrategy();
        LoanStrategy newStrategy = request.getStrategy();

        if (oldStrategy == newStrategy) {
            throw new UserApiException("Loan already uses the selected strategy", HttpStatus.BAD_REQUEST);
        }

        loan.setStrategy(newStrategy);

        loanRepository.save(loan);

        auditLogService.createAuditLog(
                securityService.getCurrentUser(),
                AuditAction.STRATEGY_OVERRIDDEN,
                "LOAN",
                loanId
        );

        log.info(
                "Loan strategy updated. loanId={}, oldStrategy={}, newStrategy={}",
                loanId,
                oldStrategy,
                newStrategy
        );

        return new LoanStrategyResponseDto(
                loanId,
                newStrategy
        );
    }

    private Loan getLoanEntity(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> {
                    log.warn(
                            "Loan not found. id={}",
                            loanId
                    );

                    return new ResourceNotFoundException("Loan not found with id: " + loanId);
                });
    }
}

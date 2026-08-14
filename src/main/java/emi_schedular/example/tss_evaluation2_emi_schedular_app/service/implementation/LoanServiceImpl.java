package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;


import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.LoanApplicationRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanTypeResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.UserFinancialProfile;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.AuditAction;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanType;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.RiskLevel;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.BusinessException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.LoanRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AuditService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.ConfigService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.EmailService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.LoanService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.strategy.StrategyDecision;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.strategy.StrategyDecisionEngine;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.strategy.impl.CalculateEmi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {

    private static final MathContext MC = new MathContext(20);
    private static final int SCALE = 2;


    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final ConfigService configService;
    private final StrategyDecisionEngine strategyDecisionEngine;
   private final AuditService auditService;
    private final EmailService emailService;

    @Override
    @Transactional
    public String applyLoan(LoanApplicationRequestDto request, String email) {
        log.info("Applying loan for user: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User"));

        if (!Boolean.TRUE.equals(user.getKycVerified())) {
            throw new BusinessException("Complete KYC first", HttpStatus.FORBIDDEN);
        }

        UserFinancialProfile financialProfile = user.getFinancialProfile();
        if (financialProfile == null) {
            throw new ResourceNotFoundException("Financial profile");
        }

        // "P" - max ACTIVE loans a borrower may hold, read straight from system config table
        int maxActiveLoans = configService.getInt("MAX_ACTIVE_LOANS");
        long activeLoans = loanRepository.countByBorrowerAndLoanStatus(user, LoanStatus.ACTIVE);
        if (activeLoans >= maxActiveLoans) {
            throw new BusinessException("Maximum " + maxActiveLoans + " active loans allowed. Please close an existing loan first.", HttpStatus.BAD_REQUEST);
        }

        validateAmountAndTenure(request.getLoanAmount(), request.getTenure());

        BigDecimal monthlyIncome = financialProfile.getMonthlyIncome();
        BigDecimal existingDebt = financialProfile.getExistingMonthlyDebt();
        LoanType loanType = request.getLoanType();

        // Estimated New EMI: a strategy-neutral reducing-balance estimate just for DTI sizing.
        // The actual EMI schedule is generated later using whichever strategy gets approved.
        BigDecimal estimatedNewEmi = estimateEmi(request.getLoanAmount(), request.getTenure(), loanType.getInterestRate());
        BigDecimal dti = existingDebt.add(estimatedNewEmi)
                .divide(monthlyIncome, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        StrategyDecision decision = strategyDecisionEngine.decide(dti, request.getTenure());

        Loan loan = new Loan();
        loan.setBorrower(user);
        loan.setLoanType(loanType);
        loan.setRequestedAmount(request.getLoanAmount());
        loan.setRequestedTenure(request.getTenure());
        loan.setMonthlyIncome(monthlyIncome);
        loan.setExistingMonthlyDebt(existingDebt);
        loan.setDti(dti);
        loan.setRiskLevel(decision.riskLevel());
        loan.setStrategy(decision.suggestedStrategy());
        loan.setInterestRate(loanType.getInterestRate());
        loan.setRemainingDebtAmount(request.getLoanAmount());
        loan.setEmiAmount(estimatedNewEmi);
        loan.setLoanStatus(LoanStatus.PENDING);
        loan = loanRepository.save(loan);

        auditService.log(user, AuditAction.LOAN_CREATED, "Loan", loan.getId());

        log.info("Loan #{} applied by {} | DTI={} | risk={} | suggestedStrategy={}",
                loan.getId(), email, dti, decision.riskLevel(), decision.suggestedStrategy());


        sendApplicationEmail(user, loan);

        if (decision.riskLevel() == RiskLevel.HIGH) {
            return "Loan application submitted (ID: " + loan.getId() + "). Your DTI of " + dti
                    + "% is high risk; this application is pending Loan Officer review and is likely to be rejected.";
        }
        return "Loan application submitted successfully (ID: " + loan.getId() + ") and is pending Loan Officer review.";
    }


    private void sendApplicationEmail(User user, Loan loan) {
        try {
            emailService.sendLoanAppliedEmail(user.getEmail(), user.getFullName(), loan.getId(),
                    loan.getRequestedAmount(), loan.getRequestedTenure());
        } catch (Exception e) {
            log.warn("Could not send loan-applied email to {} for loan #{}: {}", user.getEmail(), loan.getId(), e.getMessage());
        }
    }

    private void validateAmountAndTenure(BigDecimal amount, int tenure) {
        BigDecimal minAmount = configService.getDecimal("MIN_LOAN_AMOUNT");
        BigDecimal maxAmount = configService.getDecimal("MAX_LOAN_AMOUNT");
        int minTenure = configService.getInt("MIN_TENURE_MONTHS");
        int maxTenure = configService.getInt("MAX_TENURE_MONTHS");

        if (amount.compareTo(minAmount) < 0 || amount.compareTo(maxAmount) > 0) {
            throw new BusinessException("Loan amount must be between " + minAmount + " and " + maxAmount, HttpStatus.BAD_REQUEST);
        }
        if (tenure < minTenure || tenure > maxTenure) {
            throw new BusinessException("Tenure must be between " + minTenure + " and " + maxTenure + " months", HttpStatus.BAD_REQUEST);
        }
    }

    private BigDecimal estimateEmi(BigDecimal principal, int tenureMonths, BigDecimal annualInterestRate) {
        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(1200), MC);
        return CalculateEmi.estimateEmiCalculate(principal, tenureMonths, monthlyRate);
    }

    @Override
    public List<LoanTypeResponseDto> getAllLoanTypes() {
        return Arrays.stream(LoanType.values())
                .map(type -> LoanTypeResponseDto.builder().loanType(type.name())
                        .interestRate(type.getInterestRate()).build()).toList();
    }

    @Override
    public List<LoanResponseDto> getMyLoans(String email) {
        return loanRepository.findByBorrower_EmailOrderByCreatedAtDesc(email).stream().map(this::toLoanResponseDto).toList();
    }

    @Override
    public LoanResponseDto getMyLoanById(String email, Long loanId) {
        Loan loan = loanRepository.findByIdAndBorrower_Email(loanId, email).orElseThrow(() -> new ResourceNotFoundException("Loan"));
        return toLoanResponseDto(loan);
    }

    private LoanResponseDto toLoanResponseDto(Loan loan) {
        return LoanResponseDto.builder()
                .id(loan.getId())
                .loanType(loan.getLoanType() != null ? loan.getLoanType().name() : null)
                .requestedAmount(loan.getRequestedAmount())
                .requestedTenure(loan.getRequestedTenure())
                .dti(loan.getDti())
                .riskLevel(loan.getRiskLevel() != null ? loan.getRiskLevel().name() : null)
                .strategy(loan.getStrategy() != null ? loan.getStrategy().name() : null)
                .interestRate(loan.getInterestRate())
                .emiAmount(loan.getEmiAmount())
                .outstandingAmount(loan.getRemainingDebtAmount())
                .loanStatus(loan.getLoanStatus() != null ? loan.getLoanStatus().name() : null)
                .approvedAt(loan.getApprovedAt())
                .firstEmiDate(loan.getFirstEmiDate())
                .build();
    }

}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.Util.PanHashUtil;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.KycRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.EmiResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.PageResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.*;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.BusinessException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.EmiRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.LoanRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserFinancialProfileRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.BorrowerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class  BorrowerServiceImpl implements BorrowerService {
    private final UserRepository userRepository;
    private final EmiRepository emiRepository;
    private final LoanRepository loanRepository;
    private final UserFinancialProfileRepository userFinancialProfileRepository;

    @Override
    public String registerKyc(String email, KycRequestDto request) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User"));

        if (Boolean.TRUE.equals(user.getKycVerified())) {
            throw new BusinessException("KYC is already verified for this account", HttpStatus.CONFLICT);
        }

        String hashedPan = PanHashUtil.hashPan(request.getPan());

        // reusing one that belongs to a different account.
        Optional<UserFinancialProfile> existingFinancialProfile = userFinancialProfileRepository.findByUser_Email(email);

        boolean panChanged = existingFinancialProfile.isEmpty() || !hashedPan.equals(existingFinancialProfile.get().getPan());
        boolean aadhaarChanged = existingFinancialProfile.isEmpty() || !request.getAadhar().equals(existingFinancialProfile.get().getAadhar());

        if (panChanged && userFinancialProfileRepository.existsByPan(hashedPan)) {
            throw new BusinessException("This PAN is already registered with another account", HttpStatus.CONFLICT);
        }

        if (aadhaarChanged && userFinancialProfileRepository.existsByAadhar(request.getAadhar())) {
            throw new BusinessException("This Aadhaar is already registered with another account", HttpStatus.CONFLICT);
        }

        UserProfile profile = user.getProfile() != null ? user.getProfile() : new UserProfile();
        profile.setUser(user);
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setState(request.getState());
        profile.setPinCode(request.getPinCode());
        user.setProfile(profile);

        UserFinancialProfile financialProfile = existingFinancialProfile.orElseGet(UserFinancialProfile::new);
        financialProfile.setUser(user);
        financialProfile.setPan(hashedPan);
        financialProfile.setAadhar(request.getAadhar());
        financialProfile.setMonthlyIncome(request.getMonthlyIncome());
        financialProfile.setExistingMonthlyDebt(request.getExistingMonthlyDebt());
        user.setFinancialProfile(financialProfile);
        user.setKycVerified(true);

        userRepository.save(user);
        log.info("Kyc successful of user: " + user.getEmail());
        return "KYC details submitted and verified successfully";
    }

    @Override
    @Transactional
    public PageResponseDto<EmiResponseDto> getEmis(String email, Long loanId, Pageable pageable) {
        Page<Emi> emiPage = emiRepository.findByLoanIdAndBorrowerEmail(loanId, email, pageable);
        Page<EmiResponseDto> responsePage = emiPage.map((emi -> mapToResponseDto(emi)));
        return createPageResponse(responsePage);
    }


    @Override
    @Transactional
    public PageResponseDto<EmiResponseDto> getUpcomingEmis(String email, Long loanId, Pageable pageable) {
        Page<Emi> emiPage = emiRepository.findUpcomingEmis(loanId, email, EmiStatus.PENDING, LocalDate.now(), pageable);
        Page<EmiResponseDto> responsePage = emiPage.map(this::mapToResponseDto);
        return createPageResponse(responsePage);
    }

    @Override
    @Transactional
    public PageResponseDto<EmiResponseDto> getPaidEmis(String email, Long loanId, Pageable pageable) {
        Page<Emi> emiPage = emiRepository.findPaidEmis(loanId, email, EmiStatus.PAID, pageable);
        Page<EmiResponseDto> responsePage = emiPage.map(this::mapToResponseDto);
        return createPageResponse(responsePage);
    }
    @Override
    @Transactional
    public PageResponseDto<EmiResponseDto> getOverdueEmis(String email, Long loanId, Pageable pageable) {
        Page<Emi> emiPage = emiRepository.findOverdueEmis(loanId, email, EmiStatus.OVERDUE, LocalDate.now(), pageable);
        Page<EmiResponseDto> responsePage = emiPage.map(this::mapToResponseDto);
        return createPageResponse(responsePage);
    }

    private EmiResponseDto mapToResponseDto(Emi emi) {
        EmiResponseDto response = new EmiResponseDto();
        response.setId(emi.getId());
        response.setInstallmentNumber(emi.getInstallmentNumber());
        response.setDueDate(emi.getDueDate());
        response.setPrincipalAmount(emi.getPrincipalAmount());
        response.setInterestAmount(emi.getInterestAmount());
        response.setEmiAmount(emi.getEmiAmount());
        response.setRemainingBalance(emi.getRemainingBalance());
        response.setStatus(String.valueOf(emi.getStatus()));
        response.setPaidAt(emi.getPaidAt());
        return response;
    }

    private PageResponseDto<EmiResponseDto> createPageResponse(Page<EmiResponseDto> page) {
        return new PageResponseDto<>(page.getContent(), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}

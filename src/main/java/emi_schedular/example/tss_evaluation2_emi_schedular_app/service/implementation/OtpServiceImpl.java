package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.VerifyOtpRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.OtpVerification;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.OtpPurpose;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.UserAccountStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.OtpRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.EmailService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public void sendRegistrationOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserApiException("User not found"));
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new UserApiException("Email is already verified");
        }

        String otp = generateOtp();
        String otpHash = passwordEncoder.encode(otp);
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setUser(user);
        otpVerification.setPurpose(OtpPurpose.REGISTRATION);
        otpVerification.setOtpHash(otpHash);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpRepository.save(otpVerification);
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    @Transactional
    public void verifyRegistrationOtp(VerifyOtpRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserApiException("User not found"));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new UserApiException("Email is already verified");
        }

        OtpVerification otpVerification = otpRepository.findTopByUser_EmailAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(request.getEmail(), OtpPurpose.REGISTRATION)
                        .orElseThrow(() -> new UserApiException("OTP not found or already used"));


        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UserApiException("OTP has expired. Please request a new OTP.");
        }


        boolean validOtp = passwordEncoder.matches(request.getOtp(), otpVerification.getOtpHash());

        if (!validOtp) {
            throw new UserApiException("Invalid OTP");
        }

        otpVerification.setVerifiedAt(LocalDateTime.now());
        user.setEmailVerified(true);
        user.setStatus(UserAccountStatus.ACTIVE);
        userRepository.save(user);
    }

    private String generateOtp() {

        int otp = 100000 + secureRandom.nextInt(900000);

        return String.valueOf(otp);
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.*;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.JwtResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.OtpVerification;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.OtpPurpose;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.Role;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.UserAccountStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.OtpRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.security.JwtTokenProvider;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AuthService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.EmailService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final OtpService otpService;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public UserResponseDto register(RegistrationRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserApiException("Email already registered");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.BORROWER);
        user.setStatus(UserAccountStatus.PENDING_VERIFICATION);
        user.setEmailVerified(false);                // <-- if this field exists on User
        user = userRepository.save(user);
        otpService.sendRegistrationOtp(user.getEmail());
        user = userRepository.save(user);

        return toDto(user);
    }

    @Override
    @Transactional
    public String verifyOtp(VerifyOtpRequestDto request) {
        otpService.verifyRegistrationOtp(request);
        return "Email verified successfully. Your account is now active.";
    }


    @Override
    public JwtResponseDto login(LoginRequestDto loginDto) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            String token = tokenProvider.generateToken(authentication);

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElse(null);

            JwtResponseDto response = new JwtResponseDto();
            response.setAccessToken(token);
            response.setTokenType("Bearer");
            response.setRole(role);

            return response;

        } catch (BadCredentialsException ex) {
            throw new UserApiException("Email or password is incorrect");

        } catch (DisabledException ex) {
            // status == PENDING_VERIFICATION or INACTIVE
            throw new UserApiException("Account is not verified/active yet. Please complete OTP verification.");

        } catch (LockedException ex) {
            // status == BLOCKED
            throw new UserApiException("This account has been blocked. Please contact support.");
        }
    }


    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequestDto request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }


        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }


    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserApiException("User not found"));


        int otpNumber = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(otpNumber);
        String otpHash = passwordEncoder.encode(otp);


        OtpVerification otpVerification = new OtpVerification();

        otpVerification.setUser(user);
        otpVerification.setPurpose(OtpPurpose.PASSWORD_RESET);
        otpVerification.setOtpHash(otpHash);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpRepository.save(otpVerification);
        emailService.sendPasswordResetOtp(request.getEmail(), otp);
        log.info("Password reset OTP sent successfully to email");
    }
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserApiException("User not found"));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new UserApiException("New password and confirm password do not match");
        }


        OtpVerification otpVerification = otpRepository.findTopByUser_EmailAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(
                request.getEmail(), OtpPurpose.PASSWORD_RESET).orElseThrow(() ->
                new UserApiException("OTP not found or already used"));


        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UserApiException("OTP has expired. Please request a new password reset OTP.");
        }


        boolean validOtp = passwordEncoder.matches(request.getOtp(), otpVerification.getOtpHash());

        if (!validOtp) {
            throw new UserApiException("Invalid OTP");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        otpVerification.setVerifiedAt(LocalDateTime.now());
        userRepository.save(user);
        otpRepository.save(otpVerification);

        log.info("Password reset successfully");
    }

    private UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }
}

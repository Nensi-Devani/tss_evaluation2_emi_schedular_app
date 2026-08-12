package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.VerifyOtpRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.JwtResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.LoginRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.RegistrationRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.Role;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.UserAccountStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.security.JwtTokenProvider;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AuthService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final OtpService otpService;

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

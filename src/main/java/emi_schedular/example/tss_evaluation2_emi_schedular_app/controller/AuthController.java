package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.*;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.JwtResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegistrationRequestDto request) {
        UserResponseDto response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }


    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        JwtResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDto request, Authentication authentication) {
        String email = authentication.getName();
        authService.changePassword(email, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(("Password reset OTP sent successfully"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }
}

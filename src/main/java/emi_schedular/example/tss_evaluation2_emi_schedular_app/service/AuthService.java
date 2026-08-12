package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.VerifyOtpRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.JwtResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.LoginRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.RegistrationRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserResponseDto;

public interface AuthService {

    // Public self-registration — always creates a BORROWER.
    UserResponseDto register(RegistrationRequestDto request);

    String verifyOtp(VerifyOtpRequestDto request);


    JwtResponseDto login(LoginRequestDto loginDto);
}

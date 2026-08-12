package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.VerifyOtpRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface OtpService {

    void sendRegistrationOtp(String email);

    void verifyRegistrationOtp(VerifyOtpRequestDto request);

}

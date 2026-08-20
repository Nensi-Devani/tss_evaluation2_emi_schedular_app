package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateUserFinancialProfileRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateUserProfileRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserFinancialProfileResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserProfileResponseDto;
import jakarta.validation.Valid;

public interface ProfileService {

    UserProfileResponseDto getMyProfile(String email);
    UserProfileResponseDto updateMyProfile(String email, @Valid UpdateUserProfileRequestDto request);
    UserFinancialProfileResponseDto getMyFinancialProfile(String email);
    UserFinancialProfileResponseDto updateMyFinancialProfile(String email, @Valid UpdateUserFinancialProfileRequestDto request);

}

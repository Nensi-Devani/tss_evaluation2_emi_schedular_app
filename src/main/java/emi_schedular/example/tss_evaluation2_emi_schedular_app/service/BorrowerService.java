package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.KycRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.EmiResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.PageResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BorrowerService {
    String registerKyc(String email, @Valid KycRequestDto request);

        PageResponseDto<EmiResponseDto> getEmis(String email, Long loanId, Pageable pageable);

        PageResponseDto<EmiResponseDto> getUpcomingEmis(String email, Long loanId, Pageable pageable);

        PageResponseDto<EmiResponseDto> getPaidEmis(String email, Long loanId, Pageable pageable);

        PageResponseDto<EmiResponseDto> getOverdueEmis(String email, Long loanId, Pageable pageable);


    }


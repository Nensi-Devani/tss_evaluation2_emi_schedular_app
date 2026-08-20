package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.EmiResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import org.springframework.data.domain.Pageable;

public interface BorrowerEmiService {

    PageDto<EmiResponseDto> getLoanEmis(Long loanId, EmiStatus status, Pageable pageable);

    EmiResponseDto getNextUpcomingEmi(Long loanId);
}

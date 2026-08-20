package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.PaymentResponseDto;
import org.springframework.data.domain.Pageable;

public interface BorrowerPaymentService {

    PageDto<PaymentResponseDto> getPaymentHistory(Long loanId, Pageable pageable);

    void payEmi(Long emiId);
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.PaymentResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "emi.id", target = "emiId")
    @Mapping(source = "emi.installmentNumber", target = "installmentNumber")
    PaymentResponseDto toResponseDto(Payment payment);
}

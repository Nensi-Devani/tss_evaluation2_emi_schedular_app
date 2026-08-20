package emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserFinancialProfileResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.UserFinancialProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserFinancialProfileMapper {

    UserFinancialProfileResponseDto toResponseDto(UserFinancialProfile financialProfile);
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.EmiResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmiMapper {

    EmiResponseDto toResponseDto(Emi emi);
}

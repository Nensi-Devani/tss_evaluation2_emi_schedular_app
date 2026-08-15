package emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.BorrowerResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    BorrowerResponseDto toBorrowerResponseDto(User user);
}

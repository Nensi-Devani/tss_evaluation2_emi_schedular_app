package emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.LoanResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "borrower.id", target = "borrowerId")
    @Mapping(source = "borrower.fullName", target = "borrowerName")
    @Mapping(source = "borrower.email", target = "borrowerEmail")

    @Mapping(source = "approvedBy.id", target = "approvedById")
    @Mapping(source = "approvedBy.fullName", target = "approvedByName")

    @Mapping(source = "remainingDebtAmount", target = "remainingDebtAmount")
    LoanResponseDto toResponseDto(Loan loan);
}

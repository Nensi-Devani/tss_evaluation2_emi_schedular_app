package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.BorrowerSummaryResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserResponseDto;

import java.util.List;

public interface AdminService {


    List<UserResponseDto> getAllUsers();


    UserResponseDto getUserById(Long userId);


    List<BorrowerSummaryResponseDto> getAllBorrowers();


    BorrowerSummaryResponseDto getBorrowerById(Long userId);


    String activateBorrower(Long userId);


    String deactivateBorrower(Long userId);
}

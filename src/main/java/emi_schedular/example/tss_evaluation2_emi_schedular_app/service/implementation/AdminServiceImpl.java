package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.BorrowerSummaryResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.Role;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.UserAccountStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.BusinessException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Admin fetch all user");
        return userRepository.findAll().stream().map(this::toUserResponseDto).toList();
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User"));
        log.info("admin fetch user by id :"+ userId);
        return toUserResponseDto(user);
    }

    @Override
    public List<BorrowerSummaryResponseDto> getAllBorrowers() {
        log.info("admin fetch all borrower");
        return userRepository.findAllByRole(Role.BORROWER).stream().map(this::toBorrowerSummaryDto).toList();
    }

    @Override
    public BorrowerSummaryResponseDto getBorrowerById(Long userId) {
        User borrower = userRepository.findByIdAndRole(userId, Role.BORROWER).orElseThrow(() -> new ResourceNotFoundException("Borrower"));
        log.info("admin fetch borrower by id :" + userId);
        return toBorrowerSummaryDto(borrower);
    }

    @Override
    public String activateBorrower(Long userId) {
        User borrower = userRepository.findByIdAndRole(userId, Role.BORROWER).orElseThrow(() -> new ResourceNotFoundException("Borrower"));

        if (borrower.getStatus() == UserAccountStatus.ACTIVE) {
            throw new BusinessException("This borrower account is already active", HttpStatus.CONFLICT);
        }
        borrower.setStatus(UserAccountStatus.ACTIVE);
        userRepository.save(borrower);
        log.info("Borrower account activated by admin: {}", borrower.getEmail());
        return "Borrower account activated successfully";
    }

    @Override
    public String deactivateBorrower(Long userId) {
        User borrower = userRepository.findByIdAndRole(userId, Role.BORROWER).orElseThrow(() -> new ResourceNotFoundException("Borrower"));

        if (borrower.getStatus() == UserAccountStatus.INACTIVE) {
            throw new BusinessException("This borrower account is already inactive", HttpStatus.CONFLICT);
        }
        borrower.setStatus(UserAccountStatus.INACTIVE);
        userRepository.save(borrower);
        log.info("Borrower account deactivated by admin: {}", borrower.getEmail());
        return "Borrower account deactivated successfully";
    }



    private UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }

    private BorrowerSummaryResponseDto toBorrowerSummaryDto(User user) {
        return BorrowerSummaryResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .emailVerified(user.getEmailVerified())
                .kycVerified(user.getKycVerified())
                .build();
    }
}

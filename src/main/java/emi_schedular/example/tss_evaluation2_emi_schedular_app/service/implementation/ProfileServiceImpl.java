package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateUserFinancialProfileRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateUserProfileRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserFinancialProfileResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserProfileResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.UserFinancialProfile;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.UserProfile;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserFinancialProfileRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserProfileRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserFinancialProfileRepository userFinancialProfileRepository;

    @Override
    public UserProfileResponseDto getMyProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        UserProfile profile = userProfileRepository.findByUser_Email(email).orElseThrow(() ->new ResourceNotFoundException("user profile not found"+ email));
       log.info("Borrower fetch profile successfully");
        return toProfileResponseDto(user, profile);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateMyProfile(String email, UpdateUserProfileRequestDto request) {
        User user =  userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));;
        UserProfile profile = userProfileRepository.findByUser_Email(email).orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setState(request.getState());
        profile.setPinCode(request.getPinCode());

        userProfileRepository.save(profile);
        log.info("Borrower update profile successfully");
        return toProfileResponseDto(user, profile);
    }


    @Override
    public UserFinancialProfileResponseDto getMyFinancialProfile(String email) {
        UserFinancialProfile financialProfile = userFinancialProfileRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Financial profile not found. Please complete KYC first."));
        log.info("Borrower fetch financial profile");
        return toFinancialResponseDto(financialProfile);
    }

    @Override
    public UserFinancialProfileResponseDto updateMyFinancialProfile(String email, UpdateUserFinancialProfileRequestDto request) {
    User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    UserFinancialProfile financialProfile = userFinancialProfileRepository.findByUser_Email(email).orElseGet(() -> {
            UserFinancialProfile newProfile = new UserFinancialProfile();
            newProfile.setUser(user);
            log.info("borrower update financial profile successfully");
            return newProfile;
        });
          financialProfile.setMonthlyIncome(request.getMonthlyIncome());
        financialProfile.setExistingMonthlyDebt(request.getExistingMonthlyDebt());

     UserFinancialProfile savedProfile = userFinancialProfileRepository.save(financialProfile);
     return toFinancialResponseDto(savedProfile);

    }


    private UserProfileResponseDto toProfileResponseDto(User user, UserProfile profile) {
        UserProfileResponseDto.UserProfileResponseDtoBuilder builder = UserProfileResponseDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail());

            if (profile != null) {
            builder.dateOfBirth(profile.getDateOfBirth())
                    .address(profile.getAddress())
                    .city(profile.getCity())
                    .state(profile.getState())
                    .pinCode(profile.getPinCode());
        }
        return builder.build();
    }

    private UserFinancialProfileResponseDto toFinancialResponseDto(UserFinancialProfile financialProfile) {
        return UserFinancialProfileResponseDto.builder()
                .pan(financialProfile.getPan())
                .aadhaar(financialProfile.getAadhar())
                .monthlyIncome(financialProfile.getMonthlyIncome())
                .existingMonthlyDebt(financialProfile.getExistingMonthlyDebt())
                .build();
    }
}

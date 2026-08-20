package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {

    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String pinCode;
}

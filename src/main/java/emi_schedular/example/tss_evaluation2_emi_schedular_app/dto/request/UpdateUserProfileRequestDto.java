package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserProfileRequestDto {

    @Past(message = "dateOfBirth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 500, message = "address must not exceed 500 characters")
    private String address;

    @Size(max = 100, message = "city must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "state must not exceed 100 characters")
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "pinCode must be a 6 digit number")
    private String pinCode;
}

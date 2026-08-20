package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.UserAccountStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BorrowerResponseDto {

    private Long id;

    private String fullName;

    private String email;

    private UserAccountStatus status;
}

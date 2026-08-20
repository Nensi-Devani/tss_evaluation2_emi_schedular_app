package emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerSummaryResponseDto {

    private Long id;
    private String fullName;
    private String email;
    private String status;
    private Boolean emailVerified;
    private Boolean kycVerified;
}

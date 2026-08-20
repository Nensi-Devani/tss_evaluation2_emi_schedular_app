package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;


import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.KycRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateUserFinancialProfileRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.request.UpdateUserProfileRequestDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.EmiResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.PageResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserFinancialProfileResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserProfileResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.BorrowerService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/borrower")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BORROWER')")
public class BorrowerController {

    private final BorrowerService borrowerService;
    private final ProfileService profileService;

    @PostMapping("/register/profile")
    public ResponseEntity<String> registerKyc(@Valid @RequestBody KycRequestDto request, Authentication authentication) {
        return ResponseEntity.ok(borrowerService.registerKyc(authentication.getName(), request));
    }
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getMyProfile(authentication.getName()));
    }

    //PUT /api/profile/me
    @PutMapping("/me/profile")
    public ResponseEntity<UserProfileResponseDto> updateMyProfile(@Valid @RequestBody UpdateUserProfileRequestDto request,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(profileService.updateMyProfile(authentication.getName(), request));
    }

    // GET /api/profile/me/financial
    @GetMapping("/me/financial")
    public ResponseEntity<UserFinancialProfileResponseDto> getMyFinancialProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getMyFinancialProfile(authentication.getName()));
    }

    //  PUT /api/profile/me/financial
    @PutMapping("/me/financial")
    public ResponseEntity<UserFinancialProfileResponseDto> updateMyFinancialProfile(
            @Valid @RequestBody UpdateUserFinancialProfileRequestDto request, Authentication authentication) {
        return ResponseEntity.ok(profileService.updateMyFinancialProfile(authentication.getName(), request));
    }

    @GetMapping("/loans/{loanId}/emis")
    public ResponseEntity<PageResponseDto<EmiResponseDto>> getEmis(@PathVariable Long loanId, @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("installmentNumber").ascending());
        return ResponseEntity.ok(borrowerService.getEmis(authentication.getName(), loanId, pageable));
    }

    @GetMapping("/loans/{loanId}/emis/upcoming")
    public ResponseEntity<PageResponseDto<EmiResponseDto>> getUpcomingEmis(
            @PathVariable Long loanId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("installmentNumber").ascending());
        return ResponseEntity.ok(borrowerService.getUpcomingEmis(authentication.getName(), loanId, pageable));
    }

    @GetMapping("/loans/{loanId}/emis/paid")
    public ResponseEntity<PageResponseDto<EmiResponseDto>> getPaidEmis(
            @PathVariable Long loanId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("installmentNumber").ascending());
        return ResponseEntity.ok(borrowerService.getPaidEmis(authentication.getName(), loanId, pageable));
    }

    @GetMapping("/loans/{loanId}/emis/overdue")
    public ResponseEntity<PageResponseDto<EmiResponseDto>> getOverdueEmis(
            @PathVariable Long loanId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("installmentNumber").ascending());
        return ResponseEntity.ok(borrowerService.getOverdueEmis(authentication.getName(), loanId, pageable));
    }


}

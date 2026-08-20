package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.EmiResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.BorrowerEmiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrower/loans")
@RequiredArgsConstructor
public class BorrowerEmiController {

    private final BorrowerEmiService borrowerEmiService;

    @GetMapping("/{loanId}/emis")
    public ResponseEntity<PageDto<EmiResponseDto>> getLoanEmis(@PathVariable Long loanId, @RequestParam(required = false) EmiStatus status, Pageable pageable) {
        return ResponseEntity.ok(borrowerEmiService.getLoanEmis(loanId, status, pageable));
    }

    @GetMapping("/{loanId}/emis/upcoming")
    public ResponseEntity<EmiResponseDto> getNextUpcomingEmi(@PathVariable Long loanId) {
        return ResponseEntity.ok(borrowerEmiService.getNextUpcomingEmi(loanId));
    }
}

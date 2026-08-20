package emi_schedular.example.tss_evaluation2_emi_schedular_app.controller;


import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.BorrowerSummaryResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.UserResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;


    @GetMapping("users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }


    @GetMapping("users/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }


    @GetMapping("borrowers")
    public ResponseEntity<List<BorrowerSummaryResponseDto>> getAllBorrowers() {
        return ResponseEntity.ok(adminService.getAllBorrowers());
    }


    @GetMapping("borrowers/{userId}")
    public ResponseEntity<BorrowerSummaryResponseDto> getBorrowerById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getBorrowerById(userId));
    }


    @PatchMapping("borrowers/{userId}/activate")
    public ResponseEntity<String> activateBorrower(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.activateBorrower(userId));
    }


    @PatchMapping("borrowers/{userId}/deactivate")
    public ResponseEntity<String> deactivateBorrower(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.deactivateBorrower(userId));
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import java.math.BigDecimal;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendPasswordResetOtp(String email, String otp);
    void sendLoanAppliedEmail(String toEmail, String fullName, Long loanId, BigDecimal amount, Integer tenure);

    void sendEmiReminderEmail(
            String to,
            String borrowerName,
            Integer installmentNumber,
            String dueDate,
            String emiAmount
    );

    void sendEmiOverdueEmail(
            String to,
            String borrowerName,
            Integer installmentNumber,
            String dueDate,
            String emiAmount,
            String penalty,
            String totalAmount
    );
}

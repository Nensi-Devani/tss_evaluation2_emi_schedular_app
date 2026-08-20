package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);

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

package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Email Verification OTP");
        message.setText(
                "Hello,\n\n" +
                        "Your OTP for email verification is: " + otp + "\n\n" +
                        "This OTP is valid for 5 minutes.\n\n" +
                        "Please do not share this OTP with anyone.\n\n" +
                        "Regards,\n" +
                        "EMI Scheduler Team"
        );

        mailSender.send(message);
    }


    @Override
    public void sendPasswordResetOtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your password reset OTP is: " + otp + "\n\n" + "This OTP is valid for 5 minutes.");
        mailSender.send(message);
    }


    @Override
    public void sendLoanAppliedEmail(String toEmail, String fullName, Long loanId, BigDecimal amount, Integer tenure) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Loan Application Received - #" + loanId);
        message.setText(
                "Hello " + fullName + ",\n\n" +
                        "We have received your loan application.\n\n" +
                        "Application ID : " + loanId + "\n" +
                        "Amount         : " + amount + "\n" +
                        "Tenure         : " + tenure + " months\n\n" +
                        "Your application is currently PENDING and will be reviewed by a Loan Officer shortly.\n\n" +
                        "Regards,\n" +
                        "EMI Scheduler Team"
        );
        mailSender.send(message);
    }
}

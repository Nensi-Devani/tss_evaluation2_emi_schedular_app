package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public void sendEmiReminderEmail(
            String to,
            String borrowerName,
            Integer installmentNumber,
            String dueDate,
            String emiAmount
    ) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);

        message.setSubject("EMI Payment Reminder - Due on " + dueDate);

        message.setText(
                "Dear " + borrowerName + ",\n\n" +

                        "This is a reminder that your EMI payment " +
                        "is due soon.\n\n" +

                        "EMI Details\n" +
                        "--------------------------------\n" +
                        "EMI Number : " + installmentNumber + "\n" +
                        "Due Date   : " + dueDate + "\n" +
                        "EMI Amount : ₹" + emiAmount + "\n" +
                        "--------------------------------\n\n" +

                        "Please make your EMI payment on or before " +
                        "the due date to avoid late payment charges.\n\n" +

                        "If you have already made the payment, " +
                        "please ignore this email.\n\n" +

                        "Regards,\n" +
                        "EMI Scheduler Team"
        );

        mailSender.send(message);

        log.info(
                "EMI reminder email sent successfully. to={}, emiNumber={}",
                to,
                installmentNumber
        );
    }

    @Override
    public void sendEmiOverdueEmail(
            String to,
            String borrowerName,
            Integer installmentNumber,
            String dueDate,
            String emiAmount,
            String penalty,
            String totalAmount
    ) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);

        message.setSubject("EMI Overdue - ₹100 Late Payment Penalty");

        message.setText(
                "Dear " + borrowerName + ",\n\n" +

                        "Your EMI payment is overdue.\n\n" +

                        "EMI Details\n" +
                        "--------------------------------\n" +
                        "EMI Number    : " + installmentNumber + "\n" +
                        "Due Date      : " + dueDate + "\n" +
                        "EMI Amount    : ₹" + emiAmount + "\n" +
                        "Late Penalty  : ₹" + penalty + "\n" +
                        "Total Payable : ₹" + totalAmount + "\n" +
                        "--------------------------------\n\n" +

                        "Your EMI was not paid by the due date.\n" +
                        "A late payment penalty of ₹" + penalty +
                        " has been added.\n\n" +

                        "Please make the payment as soon as possible.\n\n" +

                        "If you have already made the payment, " +
                        "please ignore this email.\n\n" +

                        "Regards,\n" +
                        "EMI Scheduler Team"
        );

        mailSender.send(message);

        log.info(
                "EMI overdue email sent successfully. to={}, emiNumber={}",
                to,
                installmentNumber
        );
    }
}

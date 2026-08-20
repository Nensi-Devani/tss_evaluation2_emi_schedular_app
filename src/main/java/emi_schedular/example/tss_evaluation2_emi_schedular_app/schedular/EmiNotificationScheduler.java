package emi_schedular.example.tss_evaluation2_emi_schedular_app.schedular;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.EmiRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmiNotificationScheduler {

    private static final BigDecimal PENALTY = new BigDecimal("100.00");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final EmiRepository emiRepository;
    private final EmailService emailService;

    // 9 am
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Kolkata")
    @Transactional(readOnly = true)
    public void sendEmiReminderEmails() {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(2);

        log.info(
                "EMI reminder scheduler started. today={}, checkingDueDate={}",
                today,
                dueDate
        );

        List<Emi> emis = emiRepository.findEmisForReminder(
                EmiStatus.PENDING,
                LoanStatus.ACTIVE,
                dueDate
        );

        log.info(
                "Found {} EMI(s) for reminder email",
                emis.size()
        );

        for (Emi emi : emis) {
            try {
                sendReminderEmail(emi);
            } catch (Exception e) {
                log.error(
                        "Failed to send EMI reminder. emiId={}",
                        emi.getId(),
                        e
                );
            }
        }

        log.info("EMI reminder scheduler completed.");
    }

    // 9 : 10 am
    @Scheduled(cron = "0 10 9 * * *", zone = "Asia/Kolkata")
    @Transactional
    public void sendOverdueEmiEmails() {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.minusDays(1);

        log.info(
                "Overdue EMI scheduler started. today={}, checkingDueDate={}",
                today,
                dueDate
        );

        List<Emi> emis = emiRepository.findEmisForOverdueEmail(
                EmiStatus.PENDING,
                LoanStatus.ACTIVE,
                dueDate
        );

        log.info(
                "Found {} overdue EMI(s)",
                emis.size()
        );

        for (Emi emi : emis) {
            try {
                processOverdueEmi(emi);
            } catch (Exception e) {
                log.error(
                        "Failed to process overdue EMI. emiId={}",
                        emi.getId(),
                        e
                );
            }
        }

        log.info("Overdue EMI scheduler completed.");
    }

    private void sendReminderEmail(Emi emi) {
        if (emi.getLoan() == null) {
            log.warn(
                    "Loan missing for EMI. emiId={}",
                    emi.getId()
            );

            return;
        }

        if (emi.getLoan().getBorrower() == null) {
            log.warn(
                    "Borrower missing for EMI. emiId={}",
                    emi.getId()
            );

            return;
        }

        String email = emi.getLoan().getBorrower().getEmail();

        String borrowerName = emi.getLoan().getBorrower().getFullName();

        String dueDate = emi.getDueDate().format(DATE_FORMATTER);

        String emiAmount = formatAmount(emi.getEmiAmount());

        emailService.sendEmiReminderEmail(
                email,
                borrowerName,
                emi.getInstallmentNumber(),
                dueDate,
                emiAmount
        );

        log.info(
                "EMI reminder email sent. emiId={}, email={}",
                emi.getId(),
                email
        );
    }

    private void processOverdueEmi(Emi emi) {
        if (emi.getLoan() == null) {
            log.warn(
                    "Loan missing for EMI. emiId={}",
                    emi.getId()
            );

            return;
        }

        if (emi.getLoan().getBorrower() == null) {
            log.warn(
                    "Borrower missing for EMI. emiId={}",
                    emi.getId()
            );

            return;
        }

        if (emi.getStatus() != EmiStatus.PENDING) {
            log.info(
                    "EMI is not pending. Skipping overdue email. emiId={}, status={}",
                    emi.getId(),
                    emi.getStatus()
            );

            return;
        }

        BigDecimal emiAmount = emi.getEmiAmount()
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = emiAmount
                        .add(PENALTY)
                        .setScale(2, RoundingMode.HALF_UP);

        String email = emi.getLoan().getBorrower().getEmail();

        String borrowerName = emi.getLoan().getBorrower().getFullName();

        String dueDate = emi.getDueDate().format(DATE_FORMATTER);

        emailService.sendEmiOverdueEmail(
                email,
                borrowerName,
                emi.getInstallmentNumber(),
                dueDate,
                formatAmount(emiAmount),
                formatAmount(PENALTY),
                formatAmount(totalAmount)
        );

        log.info(
                "Overdue EMI email sent. emiId={}, email={}, penalty={}",
                emi.getId(),
                email,
                PENALTY
        );
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }

        return amount
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.Util;


import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.OtpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpCleanupScheduler {

    private final OtpRepository otpRepository;

    @Scheduled(cron = "${otp.cleanup.cron}")
    @Transactional
    public void cleanupOtps() {

        LocalDateTime now = LocalDateTime.now();

        long expiredDeleted = otpRepository.deleteByExpiresAtBefore(now);

        long verifiedDeleted = otpRepository.deleteByVerifiedAtIsNotNullAndVerifiedAtBefore(now.minusHours(1));

        log.info("OTP cleanup completed. Expired deleted: {}, Verified deleted: {}", expiredDeleted, verifiedDeleted);
    }
}

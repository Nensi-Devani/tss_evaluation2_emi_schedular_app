package emi_schedular.example.tss_evaluation2_emi_schedular_app.repository;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.OtpVerification;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification,Long> {

    Optional<OtpVerification>
    findTopByUser_EmailAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(String email, OtpPurpose purpose);


    @Modifying
    @Query("""
    DELETE FROM OtpVerification o
    WHERE o.expiresAt < :time
""")
    long deleteByExpiresAtBefore(@Param("time") LocalDateTime time);

    @Modifying
    @Query("""
    DELETE FROM OtpVerification o
    WHERE o.verifiedAt IS NOT NULL
      AND o.verifiedAt < :time
""")
    long deleteByVerifiedAtIsNotNullAndVerifiedAtBefore(@Param("time") LocalDateTime time);
}

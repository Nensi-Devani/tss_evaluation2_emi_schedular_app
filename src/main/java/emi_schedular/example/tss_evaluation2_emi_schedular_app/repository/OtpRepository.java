package emi_schedular.example.tss_evaluation2_emi_schedular_app.repository;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.OtpVerification;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification,Long> {

    Optional<OtpVerification> findTopByUser_EmailAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(String email, OtpPurpose purpose);
}

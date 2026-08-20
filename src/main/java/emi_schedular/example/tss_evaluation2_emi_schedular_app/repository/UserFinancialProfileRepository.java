package emi_schedular.example.tss_evaluation2_emi_schedular_app.repository;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.UserFinancialProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFinancialProfileRepository extends JpaRepository<UserFinancialProfile, Long> {

    boolean existsByPan(String pan);

    boolean existsByAadhar(String aadhar);

    Optional<UserFinancialProfile> findByUser_Email(String email);

}

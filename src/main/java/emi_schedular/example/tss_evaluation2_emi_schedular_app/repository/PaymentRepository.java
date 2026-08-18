package emi_schedular.example.tss_evaluation2_emi_schedular_app.repository;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByEmiLoanId(Long loanId, Pageable pageable);

    @Query("""
            SELECT p
            FROM Payment p
            JOIN p.emi e
            JOIN e.loan l
            WHERE l.id = :loanId
            ORDER BY p.paymentDate DESC
            """)
    Page<Payment> findPaymentHistoryByLoanId(@Param("loanId") Long loanId, Pageable pageable);

    boolean existsByEmiId(Long emiId);
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.repository;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EmiRepository extends JpaRepository<Emi, Long> {

    // ALL EMIs
    @Query("""
            SELECT e
            FROM Emi e
            WHERE e.loan.id = :loanId
              AND e.loan.borrower.email = :email
            """)
    Page<Emi> findByLoanIdAndBorrowerEmail(@Param("loanId") Long loanId, @Param("email") String email, Pageable pageable);


    // UPCOMING EMIs
    @Query("""
            SELECT e
            FROM Emi e
            WHERE e.loan.id = :loanId
              AND e.loan.borrower.email = :email
              AND e.status = :status
              AND e.dueDate >= :today
            """)
    Page<Emi> findUpcomingEmis(@Param("loanId") Long loanId, @Param("email") String email, @Param("status") EmiStatus status, @Param("today") LocalDate today, Pageable pageable);


    // PAID EMIs
    @Query("""
            SELECT e
            FROM Emi e
            WHERE e.loan.id = :loanId
              AND e.loan.borrower.email = :email
              AND e.status = :status
            """)
    Page<Emi> findPaidEmis(@Param("loanId") Long loanId, @Param("email") String email, @Param("status") EmiStatus status, Pageable pageable);


    // OVERDUE EMIs
    @Query("""
            SELECT e
            FROM Emi e
            WHERE e.loan.id = :loanId
              AND e.loan.borrower.email = :email
              AND e.status = :status
              AND e.dueDate < :today
            """)
    Page<Emi> findOverdueEmis(@Param("loanId") Long loanId, @Param("email") String email, @Param("status") EmiStatus status, @Param("today") LocalDate today, Pageable pageable);
}
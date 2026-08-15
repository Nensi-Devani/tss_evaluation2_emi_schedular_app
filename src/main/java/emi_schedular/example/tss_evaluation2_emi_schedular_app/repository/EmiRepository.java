package emi_schedular.example.tss_evaluation2_emi_schedular_app.repository;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmiRepository extends JpaRepository<Emi, Long> {

    Page<Emi> findByLoanId(Long loanId, Pageable pageable);

    @Query("""
            SELECT e
            FROM Emi e
            JOIN e.loan l
            WHERE e.status = :status
              AND l.loanStatus = :loanStatus
            ORDER BY e.dueDate ASC
            """)
    Page<Emi> findOverdueEmis(
            @Param("status") EmiStatus status,
            @Param("loanStatus") LoanStatus loanStatus,
            Pageable pageable
    );

    @Query("""
            SELECT e
            FROM Emi e
            JOIN e.loan l
            WHERE e.status = :status
              AND l.loanStatus = :loanStatus
              AND e.dueDate BETWEEN :startDate AND :endDate
            ORDER BY e.dueDate ASC
            """)
    Page<Emi> findOverdueEmisByMonth(
            @Param("status") EmiStatus status,
            @Param("loanStatus") LoanStatus loanStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    Optional<Emi> findByIdAndLoanId(Long emiId, Long loanId);
}

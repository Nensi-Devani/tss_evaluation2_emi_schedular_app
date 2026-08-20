package emi_schedular.example.tss_evaluation2_emi_schedular_app.repository;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // used by borrower endpoints: makes sure the loan actually belongs to the logged-in borrower
    Optional<Loan> findByIdAndBorrower_Email(Long id, String email);

    List<Loan> findByBorrower_EmailOrderByCreatedAtDesc(String email);

    long countByBorrowerAndLoanStatus(User borrower, LoanStatus loanStatus);

    List<Loan> findByLoanStatusOrderByCreatedAtAsc(LoanStatus loanStatus);

    /*
      Returns total EMI amount for loans of a particular borrower
     having the given loan status.
     For PENDING loans, emiAmount represents the estimated EMI.
     For ACTIVE loans, emiAmount represents the approved EMI.
     */
    @Query("""
            SELECT COALESCE(SUM(l.emiAmount), 0)
            FROM Loan l
            WHERE l.borrower = :borrower
              AND l.loanStatus = :loanStatus
            """)
    BigDecimal sumEmiByBorrowerAndLoanStatus(@Param("borrower") User borrower, @Param("loanStatus") LoanStatus loanStatus);

           
    boolean existsByIdAndBorrowerId(Long loanId, Long borrowerId);

    boolean existsByIdAndLoanStatus(Long loanId, LoanStatus loanStatus);
}

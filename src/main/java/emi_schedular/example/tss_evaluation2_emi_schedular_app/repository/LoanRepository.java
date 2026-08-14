package emi_schedular.example.tss_evaluation2_emi_schedular_app.repository;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Loan;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // used by borrower endpoints: makes sure the loan actually belongs to the logged-in borrower
    Optional<Loan> findByIdAndBorrower_Email(Long id, String email);


    List<Loan> findByBorrower_EmailOrderByCreatedAtDesc(String email);

    long countByBorrowerAndLoanStatus(User borrower, LoanStatus loanStatus);

    List<Loan> findByLoanStatusOrderByCreatedAtAsc(LoanStatus loanStatus);
}

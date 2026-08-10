package emi_schedular.example.tss_evaluation2_emi_schedular_app.entity;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanStrategy;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.LoanType;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
public class Loan extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "borrower_id",nullable = false)
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false, length = 30)
    private LoanType loanType;

    @Column(
            name = "requested_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal requestedAmount;

    @Column(name = "requested_tenure", nullable = false)
    private Integer requestedTenure;

    @Column(
            name = "monthly_income",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal monthlyIncome;

    @Column(
            name = "existing_monthly_debt",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal existingMonthlyDebt;

    @Column(precision = 5, scale = 2)
    private BigDecimal dti;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "risk_level",
            nullable = false,
            length = 20
    )
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "strategy", length = 40)
    private LoanStrategy strategy;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "emi_amount", precision = 15, scale = 2)
    private BigDecimal emiAmount;

    @Column(
            name = "outstanding_amount",
            precision = 15,
            scale = 2
    )
    private BigDecimal remainingDeptAmount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "loan_status",
            nullable = false,
            length = 20
    )
    private LoanStatus loanStatus;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "first_emi_date")
    private LocalDate firstEmiDate;

    @OneToMany(
            mappedBy = "loan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Emi> emis = new ArrayList<>();
}

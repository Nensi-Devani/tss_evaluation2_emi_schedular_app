package emi_schedular.example.tss_evaluation2_emi_schedular_app.entity;

import  emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_schedules")
@Getter
@Setter
@NoArgsConstructor
public class Emi extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(
            name = "principal_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal principalAmount;

    @Column(
            name = "interest_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal interestAmount;

    @Column(
            name = "emi_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal emiAmount;

    @Column(
            name = "remaining_balance",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal remainingBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmiStatus status = EmiStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDate paidAt;

    @OneToOne(mappedBy = "emi")
    private Payment payment;
}

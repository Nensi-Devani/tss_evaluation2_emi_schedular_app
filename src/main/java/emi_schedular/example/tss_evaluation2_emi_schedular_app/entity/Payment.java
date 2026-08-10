package emi_schedular.example.tss_evaluation2_emi_schedular_app.entity;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity{

    @OneToOne
    @JoinColumn(
            name = "emi_schedule_id",
            nullable = false,
            unique = true
    )
    private Emi emi;

    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
}

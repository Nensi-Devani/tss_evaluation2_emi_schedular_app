package emi_schedular.example.tss_evaluation2_emi_schedular_app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "user_financial_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserFinancialProfile extends BaseEntity{

    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(nullable = true, unique = true)
    private String pan;

    @Column(nullable = true, unique = true)
    private String aadhar;

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

}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.entity;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.Role;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.UserAccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity{

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.BORROWER;

    @Enumerated(EnumType.STRING)
    private UserAccountStatus status = UserAccountStatus.PENDING_VERIFICATION;

    private Boolean emailVerified = false;

    private Boolean KycVerified = false;

//    relationships

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private UserProfile profile;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private UserFinancialProfile financialProfile;

//    @OneToMany(mappedBy = "user")
//    private List<LoginAttempt> loginAttempts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<OtpVerification> otpVerifications = new ArrayList<>();

    @OneToMany(mappedBy = "borrower")
    private List<Loan> loans = new ArrayList<>();
}

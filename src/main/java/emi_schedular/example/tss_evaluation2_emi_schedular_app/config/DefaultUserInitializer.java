package emi_schedular.example.tss_evaluation2_emi_schedular_app.config;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.Role;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.UserAccountStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.full-name}")
    private String adminFullName;

    @Value("${app.default-admin.email}")
    private String adminEmail;

    @Value("${app.default-admin.password}")
    private String adminPassword;

    @Value("${app.default-loan-officer.full-name}")
    private String loanOfficerFullName;

    @Value("${app.default-loan-officer.email}")
    private String loanOfficerEmail;

    @Value("${app.default-loan-officer.password}")
    private String loanOfficerPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        createAdminIfNotExists();

        createLoanOfficerIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (userRepository.existsByRole(Role.ADMIN)) {
            System.out.println("Admin already exists. Skipping admin initialization.");
            return;
        }

        User admin = new User();

        admin.setFullName(adminFullName);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserAccountStatus.ACTIVE);
        admin.setEmailVerified(true);

        userRepository.save(admin);

        System.out.println("Default admin created successfully.");
    }

    private void createLoanOfficerIfNotExists() {
        if (userRepository.existsByRole(Role.LOAN_OFFICER)) {
            System.out.println("Loan Officer already exists. Skipping initialization.");
            return;
        }

        User loanOfficer = new User();

        loanOfficer.setFullName(loanOfficerFullName);
        loanOfficer.setEmail(loanOfficerEmail);
        loanOfficer.setPassword(passwordEncoder.encode(loanOfficerPassword));
        loanOfficer.setRole(Role.LOAN_OFFICER);
        loanOfficer.setStatus(UserAccountStatus.ACTIVE);
        loanOfficer.setEmailVerified(true);

        userRepository.save(loanOfficer);

        System.out.println("Default loan officer created successfully.");
    }
}

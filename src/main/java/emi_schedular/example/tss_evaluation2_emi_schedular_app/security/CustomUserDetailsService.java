package emi_schedular.example.tss_evaluation2_emi_schedular_app.security;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.UserAccountStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Loads a User by email and adapts it into a Spring Security UserDetails.
 *
 * The account flags below are derived directly from UserAccountStatus, so
 * the AuthenticationManager itself rejects a bad-state login before any
 * controller code runs:
 *
 *   PENDING_VERIFICATION -> enabled = false   -> DisabledException
 *   INACTIVE              -> enabled = false   -> DisabledException
 *   BLOCKED                -> accountNonLocked = false -> LockedException
 *   ACTIVE                  -> enabled = true, accountNonLocked = true -> normal login
 *
 * This means a freshly-registered BORROWER (status = PENDING_VERIFICATION,
 * emailVerified = false) genuinely cannot obtain a JWT until whatever
 * verifies the OtpVerification(purpose = REGISTRATION) flips their status
 * to ACTIVE.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // "ROLE_" prefix is the Spring Security convention that lets
        // hasRole("ADMIN") in SecurityConfig match an authority of
        // "ROLE_ADMIN" without every matcher having to spell it out.
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        boolean accountNonLocked = user.getStatus() != UserAccountStatus.BLOCKED;
        boolean enabled = user.getStatus() == UserAccountStatus.ACTIVE;

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                enabled,          // enabled
                true,              // accountNonExpired
                true,              // credentialsNonExpired
                accountNonLocked,  // accountNonLocked
                authorities
        );
    }
}

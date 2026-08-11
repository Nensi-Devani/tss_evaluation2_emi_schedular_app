package emi_schedular.example.tss_evaluation2_emi_schedular_app.config;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.security.CustomUserDetailsService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.security.JwtAccessDeniedHandler;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.security.JwtAuthenticationEntryPoint;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central place the whole role-based-access flow is wired up.
 *
 * Role model (see enums.Role):
 *   BORROWER      -> self-registers via /api/auth/register; manages their
 *                     own profile/loans/EMIs under /api/borrower/**.
 *   LOAN_OFFICER  -> reviews/approves loans under /api/loan-officer/**;
 *                     created only by an ADMIN.
 *   ADMIN          -> full platform administration under /api/admin/**,
 *                     including staff creation; created only by an
 *                     existing ADMIN (or seeded at startup).
 *   SYSTEM          -> non-interactive/service account (e.g. the scheduled
 *                     job that marks EMIs overdue) under /api/system/**.
 *                     Never created through a public endpoint — seed it
 *                     directly in the DB.
 *
 * @EnableMethodSecurity turns on @PreAuthorize so individual controller
 * methods can layer their own checks on top of these coarse URL rules —
 * e.g. "a BORROWER may fetch /api/borrower/loans/{id} only if that loan's
 * borrower_id is their own user id." A URL pattern alone can never express
 * that; it has to be a method-level check against the authenticated
 * principal, done in the service/controller layer.
 */
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter authenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println(new BCryptPasswordEncoder().encode("admin123"));
        return new BCryptPasswordEncoder();

    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                // Public: no token required
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                // BORROWER ("User") only
                .requestMatchers("/api/user/**").hasRole("BORROWER")

                // LOAN_OFFICER ("loan_manager") only
                .requestMatchers("/api/loan-manager/**").hasRole("LOAN_OFFICER")

                // ADMIN only — includes staff creation
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Anything else just needs a valid token, any role
                .anyRequest().authenticated()
        );

        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(authenticationEntryPoint));

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
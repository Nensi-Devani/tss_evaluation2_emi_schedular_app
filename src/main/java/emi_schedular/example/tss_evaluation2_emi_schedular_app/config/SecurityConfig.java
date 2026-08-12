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

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter authenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
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
                .requestMatchers("/api/auth/register", "/api/auth/login" , "/api/auth/verify-otp").permitAll()

                // BORROWER ("User") only
                .requestMatchers("/api/borrower/**").hasRole("BORROWER")

                // LOAN_OFFICER ("loan_manager") only
                .requestMatchers("/api/loan-officer/**").hasRole("LOAN_OFFICER")

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

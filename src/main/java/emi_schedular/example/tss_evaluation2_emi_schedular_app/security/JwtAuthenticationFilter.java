package emi_schedular.example.tss_evaluation2_emi_schedular_app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs once per request, ahead of Spring Security's own
 * UsernamePasswordAuthenticationFilter. Reads the "Authorization: Bearer
 * <token>" header, validates it, and — if valid — populates the
 * SecurityContext so downstream hasRole()/@PreAuthorize checks see an
 * authenticated user with the right authority.
 *
 * IMPORTANT FIX vs. a naive implementation: token errors are caught here
 * on purpose. If validateToken()'s UserApiException were allowed to
 * propagate, it would escape the filter chain as an unhandled 500 instead
 * of a clean 401 — this is the #1 way JWT filters "break in production"
 * the moment a client sends an expired or tampered token. Instead we just
 * leave the SecurityContext empty; the request falls through as
 * anonymous, and:
 *   - public endpoints (permitAll in SecurityConfig) still work fine
 *   - protected endpoints correctly get a clean 401 from
 *     JwtAuthenticationEntryPoint
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            String email = jwtTokenProvider.getEmail(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }

        return null;
    }
}

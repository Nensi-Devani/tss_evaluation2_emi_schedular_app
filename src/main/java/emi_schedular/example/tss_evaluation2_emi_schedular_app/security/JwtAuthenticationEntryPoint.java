package emi_schedular.example.tss_evaluation2_emi_schedular_app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fires whenever an unauthenticated request hits a protected endpoint —
 * no token supplied at all, or a token that JwtAuthenticationFilter
 * rejected. Returns a clean, parseable 401 JSON body instead of a blank
 * response or the default HTML error page.
 *
 * Distinct from JwtAccessDeniedHandler (403): this is "who are you?",
 * that is "I know who you are, but you're not allowed here."
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}

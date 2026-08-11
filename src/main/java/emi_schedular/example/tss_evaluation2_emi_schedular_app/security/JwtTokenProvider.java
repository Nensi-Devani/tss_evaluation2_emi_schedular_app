package emi_schedular.example.tss_evaluation2_emi_schedular_app.security;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.UserApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Issues and validates the JWTs that carry a user's identity + role.
 * jwtSecret MUST be a Base64-encoded string that decodes to >= 256 bits
 * (32 bytes) for HS256 — a short plain-text string here will throw at
 * startup or (worse) silently produce a weak key.
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        // authorities holds exactly one entry, e.g. "ROLE_ADMIN" — embed it
        // as a claim so the filter can (optionally) read the role straight
        // off the token without a DB hit, and so client apps can branch UI
        // on it after login.
        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() -> new UserApiException("Authenticated user has no role"))
                .getAuthority();

        return Jwts.builder()
                .claims()
                .subject(email)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .and()
                .claim("role", role)
                .signWith(key())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;

        } catch (MalformedJwtException ex) {
            throw new UserApiException("Invalid JWT token");

        } catch (ExpiredJwtException ex) {
            throw new UserApiException("Expired JWT token");

        } catch (UnsupportedJwtException ex) {
            throw new UserApiException("Unsupported JWT token");

        } catch (IllegalArgumentException ex) {
            throw new UserApiException("JWT claims string is empty");

        } catch (Exception ex) {
            throw new UserApiException("Invalid credentials");
        }
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getEmail(String token) {
        Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }
}

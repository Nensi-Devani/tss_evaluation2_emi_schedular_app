package emi_schedular.example.tss_evaluation2_emi_schedular_app.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status,
            HttpServletRequest request, Map<String, String> errors) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(message);
        error.setStatus(status.value());
        error.setPath(request.getRequestURI());
        error.setTimestamp(LocalDateTime.now());
        error.setErrors(errors);

        return ResponseEntity.status(status).body(error);
    }

    // Covers UserApiException itself PLUS every subclass you uploaded:
    // UserNotFoundException, UserAlreadyExistsException,
    // ResourceNotFoundException, InvalidPageException,
    // EmailSendingException, BusinessException, and your custom
    // exception.AccessDeniedException. Each one already carries the right
    // HttpStatus via its constructor, so we just read it off ex.getStatus().
    @ExceptionHandler(UserApiException.class)
    public ResponseEntity<ErrorResponse> handleUserApiException(
            UserApiException ex, HttpServletRequest request) {

        log.warn("UserApiException: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getStatus(), request, null);
    }

    // Thrown by Spring Security itself (@PreAuthorize / hasRole(...) checks
    // failing on an authenticated-but-unauthorized user). This is the
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied: {}", ex.getMessage());
        return buildErrorResponse(
                "You do not have permission to access this resource",
                HttpStatus.FORBIDDEN, request, null);
    }

    // Authentication failures raised directly during the login flow, in
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        return buildErrorResponse("Email or password is incorrect",
                HttpStatus.UNAUTHORIZED, request, null);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(
            DisabledException ex, HttpServletRequest request) {
        return buildErrorResponse("Account is not verified/active yet. Please complete OTP verification.",
                HttpStatus.FORBIDDEN, request, null);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLocked(
            LockedException ex, HttpServletRequest request) {
        return buildErrorResponse("This account has been blocked. Please contact support.",
                HttpStatus.FORBIDDEN, request, null);
    }

    // Catch-all for any other AuthenticationException subtype not handled
    // above (AccountExpiredException, CredentialsExpiredException, etc.)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        return buildErrorResponse("Authentication failed",
                HttpStatus.UNAUTHORIZED, request, null);
    }

    // @Valid failures on @RequestBody DTOs (e.g. RegistrationRequestDto,
    // LoginRequestDto). Field-level messages go in the "errors" map so the
    // frontend can highlight individual inputs.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return buildErrorResponse("Validation failed", HttpStatus.BAD_REQUEST, request, errors);
    }

    // e.g. a path variable like /api/loans/{id} receiving "abc" instead of
    // a Long — without this it surfaces as an ugly 500.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String message = String.format("'%s' should be a valid '%s'",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "value");
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, request, null);
    }

    // Final safety net — anything unanticipated (NPEs, DB errors that
    // weren't wrapped, etc.) still comes back as clean JSON instead of a
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception at {}", request.getRequestURI(), ex);
        return buildErrorResponse("Something went wrong. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR, request, null);
    }
}
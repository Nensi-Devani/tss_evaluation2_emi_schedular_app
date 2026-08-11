package emi_schedular.example.tss_evaluation2_emi_schedular_app.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserApiException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
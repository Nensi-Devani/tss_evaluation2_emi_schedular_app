package emi_schedular.example.tss_evaluation2_emi_schedular_app.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends UserApiException{
    public AccessDeniedException(String message) {
        super(message,HttpStatus.FORBIDDEN);
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends UserApiException{
    public BusinessException(String message, HttpStatus status) {
        super(message,status);
    }
}

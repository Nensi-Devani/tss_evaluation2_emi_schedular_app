package emi_schedular.example.tss_evaluation2_emi_schedular_app.exception;

import org.springframework.http.HttpStatus;

public class InvalidPageException extends UserApiException{
    public InvalidPageException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

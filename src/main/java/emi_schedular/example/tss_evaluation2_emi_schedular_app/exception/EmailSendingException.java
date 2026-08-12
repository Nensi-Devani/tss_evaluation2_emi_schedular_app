package emi_schedular.example.tss_evaluation2_emi_schedular_app.exception;

import org.springframework.http.HttpStatus;

public class EmailSendingException extends UserApiException{
    public EmailSendingException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

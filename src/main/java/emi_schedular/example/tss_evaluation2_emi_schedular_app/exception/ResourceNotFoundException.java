package emi_schedular.example.tss_evaluation2_emi_schedular_app.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends UserApiException{
    public ResourceNotFoundException(String resource) {
        super(resource, HttpStatus.NOT_FOUND);
    }
}

package emi_schedular.example.tss_evaluation2_emi_schedular_app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserApiException extends RuntimeException {

    private final HttpStatus status;

    public UserApiException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public UserApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
 
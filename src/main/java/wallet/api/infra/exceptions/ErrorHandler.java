package wallet.api.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleBadRequestError(MethodArgumentNotValidException e) {
        var errorList = e.getFieldErrors();
        return ResponseEntity.badRequest().body(errorList.stream().map(BadRequestValidationErrors::new));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationError(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthenticationError("Unauthorized", "Invalid email or password"));
    }

    private record AuthenticationError(String error, String msg) {
    }


    private record BadRequestValidationErrors(String field, String msg) {
        public BadRequestValidationErrors(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

}

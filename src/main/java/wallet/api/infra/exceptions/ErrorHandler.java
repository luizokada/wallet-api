package wallet.api.infra.exceptions;

import org.springframework.http.ResponseEntity;
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


    private record BadRequestValidationErrors(String field, String msg) {
        public BadRequestValidationErrors(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}

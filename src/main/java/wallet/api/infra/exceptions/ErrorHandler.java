package wallet.api.infra.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wallet.api.errors.ai.ClassificationRateLimitedError;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleBadRequestError(MethodArgumentNotValidException e) {
        var errorList = e.getFieldErrors();
        return ResponseEntity.badRequest().body(errorList.stream().map(BadRequestValidationErrors::new));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationError(ConstraintViolationException e) {
        var errorList = e.getConstraintViolations().stream()
                .map(BadRequestValidationErrors::new)
                .toList();
        return ResponseEntity.badRequest().body(errorList);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationError(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthenticationError("Unauthorized", "Invalid email or password"));
    }

    @ExceptionHandler(ClassificationRateLimitedError.class)
    public ResponseEntity<Object> handleClassificationRateLimited(ClassificationRateLimitedError e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(e.getRetryAfterSeconds()))
                .body(new RateLimitError(e.getMessage(), e.getRetryAfterSeconds()));
    }

    private record RateLimitError(String message, long retryAfterSeconds) {
    }

    private record AuthenticationError(String error, String msg) {
    }


    private record BadRequestValidationErrors(String field, String msg) {
        public BadRequestValidationErrors(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }

        public BadRequestValidationErrors(ConstraintViolation<?> violation) {
            this(lastPathSegment(violation.getPropertyPath().toString()), violation.getMessage());
        }

        private static String lastPathSegment(String propertyPath) {
            var separator = propertyPath.lastIndexOf('.');
            return separator < 0 ? propertyPath : propertyPath.substring(separator + 1);
        }
    }

}

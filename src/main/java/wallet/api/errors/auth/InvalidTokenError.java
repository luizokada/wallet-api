package wallet.api.errors.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.UNAUTHORIZED, reason = "Invalid Token")
public class InvalidTokenError extends RuntimeException {
    public InvalidTokenError() {
        super("Invalid Token");
    }
}

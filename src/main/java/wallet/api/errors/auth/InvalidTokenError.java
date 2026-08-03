package wallet.api.errors.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.UNAUTHORIZED, reason = "Invalid Token")
public class InvalidTokenError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidTokenError() {
        super("Invalid Token");
    }
}

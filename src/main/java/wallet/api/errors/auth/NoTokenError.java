package wallet.api.errors.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST , reason = "No token provided")
public class NoTokenError extends RuntimeException {
    public NoTokenError() {
        super("No token provided");
    }

}

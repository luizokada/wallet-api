package wallet.api.errors.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid or expired reset token")
public class InvalidResetTokenError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidResetTokenError() {
        super("Invalid or expired reset token");
    }
}

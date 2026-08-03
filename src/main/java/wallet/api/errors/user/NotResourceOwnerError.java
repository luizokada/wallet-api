package wallet.api.errors.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "You can only modify your own user")
public class NotResourceOwnerError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotResourceOwnerError() {
        super("You can only modify your own user");
    }
}

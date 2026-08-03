package wallet.api.errors.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Email already exists")
public class UserEmailError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserEmailError() {
        super("Email already exists");
    }
}

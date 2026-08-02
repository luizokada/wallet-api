package wallet.api.errors.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "You can only modify your own transactions")
public class NotTransactionOwnerError extends RuntimeException {
    public NotTransactionOwnerError() {
        super("You can only modify your own transactions");
    }
}

package wallet.api.errors.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Transaction not found")
public class TransactionNotFound extends RuntimeException {
    private static final long serialVersionUID = 1L;


}

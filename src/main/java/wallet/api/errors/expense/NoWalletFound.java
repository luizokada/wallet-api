package wallet.api.errors.expense;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No wallet found")
public class NoWalletFound extends RuntimeException {

}

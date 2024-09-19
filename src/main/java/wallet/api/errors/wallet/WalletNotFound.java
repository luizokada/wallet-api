package wallet.api.errors.wallet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Wallet not found")
public class WalletNotFound extends RuntimeException {

}

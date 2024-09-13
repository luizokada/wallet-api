package wallet.api.errors.wallet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST , reason = "user already has wallet")
public class UserAlreadyHasWallet  extends RuntimeException{
}

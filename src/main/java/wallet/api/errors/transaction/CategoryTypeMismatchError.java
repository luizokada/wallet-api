package wallet.api.errors.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "category type does not match transaction type")
public class CategoryTypeMismatchError extends RuntimeException {
    public CategoryTypeMismatchError() {
        super("category type does not match transaction type");
    }
}

package wallet.api.errors.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Default categories cannot be edited")
public class DefaultCategoryNotEditable extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DefaultCategoryNotEditable() {
        super("Default categories cannot be edited");
    }
}

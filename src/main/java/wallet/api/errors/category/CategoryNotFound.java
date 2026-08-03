package wallet.api.errors.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Category not found")
public class CategoryNotFound extends RuntimeException {
    private static final long serialVersionUID = 1L;

}

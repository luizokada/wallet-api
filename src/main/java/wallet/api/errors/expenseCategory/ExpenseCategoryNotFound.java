package wallet.api.errors.expenseCategory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Category not found")
public class ExpenseCategoryNotFound extends RuntimeException {
}

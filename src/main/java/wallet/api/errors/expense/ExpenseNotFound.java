package wallet.api.errors.expense;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND , reason = "Expense not found")
public class ExpenseNotFound extends RuntimeException {

}

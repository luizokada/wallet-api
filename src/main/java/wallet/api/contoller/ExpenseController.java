package wallet.api.contoller;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.expense.dto.CreateExpenseDTO;
import wallet.api.domain.expense.dto.ExpenseToApiViewDto;
import wallet.api.domain.expense.dto.UpdateExpenseDTO;
import wallet.api.domain.expense.service.ExpenseService;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryToApiVIewDTO;
import wallet.api.domain.user.entity.User;

@RestController
@RequestMapping("expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseToApiViewDto> createExpense(@AuthenticationPrincipal User user, @RequestBody @Valid CreateExpenseDTO body, UriComponentsBuilder uriBuild) {
        var createdExpense = this.expenseService.createExpense(user,body);

        var uri = uriBuild.path("/expense/{id}").buildAndExpand(createdExpense.getId()).toUri();

        ExpenseCateogryToApiVIewDTO categoryJson = null;
        if(createdExpense.getCategory() != null) {
            categoryJson = new ExpenseCateogryToApiVIewDTO(createdExpense.getCategory());
        }
        var jsonToReturn = new ExpenseToApiViewDto(createdExpense,categoryJson );

        return ResponseEntity.created(uri).body(jsonToReturn);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExpenseToApiViewDto> updateExpense(@AuthenticationPrincipal User user,@PathVariable String id, @RequestBody @Valid UpdateExpenseDTO payload) {

        var updatedExpense = this.expenseService.updateExpense(id, user, payload);
        ExpenseCateogryToApiVIewDTO categoryJson = null;
        if(updatedExpense.getCategory() != null) {
            categoryJson = new ExpenseCateogryToApiVIewDTO(updatedExpense.getCategory());
        }
        var jsonToReturn = new ExpenseToApiViewDto(updatedExpense,categoryJson);
        return ResponseEntity.ok().body(jsonToReturn);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseCategory(@PathVariable String id) {
        expenseService.deleteExpense(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseToApiViewDto> getExpenseCategoryById(@PathVariable String id) {
        var expense = expenseService.getExpense(id);
        ExpenseCateogryToApiVIewDTO categoryJson = null;
        if(expense.getCategory() != null) {
            categoryJson = new ExpenseCateogryToApiVIewDTO(expense.getCategory());
        }
        var jsonToReturn = new ExpenseToApiViewDto(expense,categoryJson);
        return ResponseEntity.ok().body(jsonToReturn);
    }
}

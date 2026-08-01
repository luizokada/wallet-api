package wallet.api.contoller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Expense",
        description = "Endpoints for managing expenses"
)
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @Operation(summary = "Create expense", description = "Creates an expense in the authenticated user's wallet, optionally linked to a category.")
    @ApiResponse(responseCode = "201", description = "Expense created")
    @ApiResponse(responseCode = "404", description = "User has no wallet or category not found")
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
    @Operation(summary = "Update expense", description = "Updates an expense's data (amount, description, category, date).")
    @ApiResponse(responseCode = "200", description = "Expense updated")
    @ApiResponse(responseCode = "404", description = "Expense not found")
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
    @Operation(summary = "Delete expense", description = "Deletes an expense by id.")
    @ApiResponse(responseCode = "200", description = "Expense deleted")
    @ApiResponse(responseCode = "404", description = "Expense not found")
    public ResponseEntity<Void> deleteExpenseCategory(@PathVariable String id) {
        expenseService.deleteExpense(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense", description = "Returns an expense by id.")
    @ApiResponse(responseCode = "200", description = "Expense data")
    @ApiResponse(responseCode = "404", description = "Expense not found")
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

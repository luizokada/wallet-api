package wallet.api.contoller;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.expense.dto.CreateExpenseDTO;
import wallet.api.domain.expense.dto.ExpenseToApiViewDto;
import wallet.api.domain.expense.service.ExpenseService;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryToApiVIewDTO;
import wallet.api.domain.user.entity.User;

import java.util.List;

@RestController
@RequestMapping("expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseToApiViewDto> createExpenseCategory(@AuthenticationPrincipal User user, @RequestBody @Valid CreateExpenseDTO body, UriComponentsBuilder uribuild) {
        var createdExpense = this.expenseService.createExpense(user,body);

        var uri = uribuild.path("/expense/{id}").buildAndExpand(createdExpense.getId()).toUri();

        ExpenseCateogryToApiVIewDTO categoryJson = null;
        if(createdExpense.getExpense_cateogories() != null) {
            categoryJson = new ExpenseCateogryToApiVIewDTO(createdExpense.getExpense_cateogories());
        }
        var jsonToReturn = new ExpenseToApiViewDto(createdExpense,categoryJson );

        return ResponseEntity.created(uri).body(jsonToReturn);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExpenseCateogryToApiVIewDTO> updateExpenseCategory(@PathVariable Long id, @RequestBody @Valid ExpenseCateogryToApiVIewDTO expenseCateogryToApiVIewDTO) {

        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseCategory(@PathVariable Long id) {

        return null;
    }
    @GetMapping
    public ResponseEntity<List<ExpenseCateogryToApiVIewDTO>> getExpenseCategory() {

        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCateogryToApiVIewDTO> getExpenseCategoryById(@PathVariable Long id) {

        return null;
    }
}

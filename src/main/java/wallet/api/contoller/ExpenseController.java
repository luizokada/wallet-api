package wallet.api.contoller;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryToApiVIewDTO;

import java.util.List;

@RestController
@RequestMapping("expense")
public class ExpenseController {

    @PostMapping
    public ResponseEntity<ExpenseCateogryToApiVIewDTO> createExpenseCategory(@RequestBody @Valid ExpenseCateogryToApiVIewDTO expenseCateogryToApiVIewDTO) {

        return null;
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

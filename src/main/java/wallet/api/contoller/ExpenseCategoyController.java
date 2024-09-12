package wallet.api.contoller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryBodyDTO;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryToApiVIewDTO;
import wallet.api.domain.expenseCategory.dtos.UpdateExpenseCategoryDTO;
import wallet.api.domain.expenseCategory.service.ExpenseCategoryService;

import java.util.List;

@RestController
@RequestMapping("expense-category")
public class ExpenseCategoyController {

    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseCategoyController(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @PostMapping
    public ResponseEntity<ExpenseCateogryToApiVIewDTO> createExpenseCategory(@RequestBody @Valid ExpenseCateogryBodyDTO body, UriComponentsBuilder uriComponentsBuilder) {
        var expenseCategory = expenseCategoryService.createExpenseCategory(body);
        var uri = uriComponentsBuilder.path("/expense-category/{id}").buildAndExpand(expenseCategory.getId()).toUri();
        return ResponseEntity.created(uri).body(new ExpenseCateogryToApiVIewDTO(expenseCategory));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExpenseCateogryToApiVIewDTO> updateExpenseCategory(@PathVariable String id, @RequestBody @Valid UpdateExpenseCategoryDTO body) {
        var expenseCategory = expenseCategoryService.updateExpenseCategory(id, body);
        return ResponseEntity.ok(new ExpenseCateogryToApiVIewDTO(expenseCategory));
    }


    @GetMapping
    public ResponseEntity<List<ExpenseCateogryToApiVIewDTO>> getExpenseCategory() {
        var expenseCategories = expenseCategoryService.getAllExpenseCategories();
        return ResponseEntity.ok(ExpenseCateogryToApiVIewDTO.toList(expenseCategories));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCateogryToApiVIewDTO> getExpenseCategoryById(@PathVariable String id) {
        var expenseCategory = expenseCategoryService.getExpenseCategoryById(id);
        return ResponseEntity.ok(new ExpenseCateogryToApiVIewDTO(expenseCategory));
    }
}

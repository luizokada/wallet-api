package wallet.api.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Expense Category",
        description = "Endpoints for managing expense categories"
)
public class ExpenseCategoyController {

    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseCategoyController(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Creates a new expense category.")
    @ApiResponse(responseCode = "201", description = "Category created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<ExpenseCateogryToApiVIewDTO> createExpenseCategory(@RequestBody @Valid ExpenseCateogryBodyDTO body, UriComponentsBuilder uriComponentsBuilder) {
        var expenseCategory = expenseCategoryService.createExpenseCategory(body);
        var uri = uriComponentsBuilder.path("/expense-category/{id}").buildAndExpand(expenseCategory.getId()).toUri();
        return ResponseEntity.created(uri).body(new ExpenseCateogryToApiVIewDTO(expenseCategory));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update category", description = "Updates an expense category's name/description.")
    @ApiResponse(responseCode = "200", description = "Category updated")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<ExpenseCateogryToApiVIewDTO> updateExpenseCategory(@PathVariable String id, @RequestBody @Valid UpdateExpenseCategoryDTO body) {
        var expenseCategory = expenseCategoryService.updateExpenseCategory(id, body);
        return ResponseEntity.ok(new ExpenseCateogryToApiVIewDTO(expenseCategory));
    }


    @GetMapping
    @Operation(summary = "List categories", description = "Lists all expense categories.")
    @ApiResponse(responseCode = "200", description = "List of categories")
    public ResponseEntity<List<ExpenseCateogryToApiVIewDTO>> getExpenseCategory() {
        var expenseCategories = expenseCategoryService.getAllExpenseCategories();
        return ResponseEntity.ok(ExpenseCateogryToApiVIewDTO.toList(expenseCategories));

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category", description = "Returns an expense category by id.")
    @ApiResponse(responseCode = "200", description = "Category data")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<ExpenseCateogryToApiVIewDTO> getExpenseCategoryById(@PathVariable String id) {
        var expenseCategory = expenseCategoryService.getExpenseCategoryById(id);
        return ResponseEntity.ok(new ExpenseCateogryToApiVIewDTO(expenseCategory));
    }
}

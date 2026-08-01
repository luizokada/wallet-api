package wallet.api.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.category.dtos.CategoryToApiViewDTO;
import wallet.api.domain.category.dtos.CreateCategoryDTO;
import wallet.api.domain.category.dtos.UpdateCategoryDTO;
import wallet.api.domain.category.service.CategoryService;
import wallet.api.domain.transaction.entity.TransactionType;

import java.util.List;

@RestController
@RequestMapping("category")
@Tag(
        name = "Category",
        description = "Endpoints for managing transaction categories (typed as EXPENSE or INCOME)"
)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Creates a category typed as EXPENSE or INCOME. The type is immutable after creation.")
    @ApiResponse(responseCode = "201", description = "Category created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<CategoryToApiViewDTO> createCategory(@RequestBody @Valid CreateCategoryDTO body, UriComponentsBuilder uriComponentsBuilder) {
        var category = categoryService.createCategory(body);
        var uri = uriComponentsBuilder.path("/category/{id}").buildAndExpand(category.getId()).toUri();
        return ResponseEntity.created(uri).body(new CategoryToApiViewDTO(category));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update category", description = "Updates a category's name/description. The type cannot be changed.")
    @ApiResponse(responseCode = "200", description = "Category updated")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<CategoryToApiViewDTO> updateCategory(@PathVariable String id, @RequestBody @Valid UpdateCategoryDTO body) {
        var category = categoryService.updateCategory(id, body);
        return ResponseEntity.ok(new CategoryToApiViewDTO(category));
    }


    @GetMapping
    @Operation(summary = "List categories", description = "Lists all categories, optionally filtered by type (?type=EXPENSE|INCOME).")
    @ApiResponse(responseCode = "200", description = "List of categories")
    public ResponseEntity<List<CategoryToApiViewDTO>> getCategories(@RequestParam(required = false) TransactionType type) {
        var categories = categoryService.getAllCategories(type);
        return ResponseEntity.ok(CategoryToApiViewDTO.toList(categories));

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category", description = "Returns a category by id.")
    @ApiResponse(responseCode = "200", description = "Category data")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<CategoryToApiViewDTO> getCategoryById(@PathVariable String id) {
        var category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new CategoryToApiViewDTO(category));
    }
}

package wallet.api.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.category.dtos.CategoryToApiViewDTO;
import wallet.api.domain.category.dtos.CreateCategoryDTO;
import wallet.api.domain.category.dtos.UpdateCategoryDTO;
import wallet.api.domain.category.service.CategoryService;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.user.entity.User;

import java.util.List;

@RestController
@RequestMapping("category")
@Tag(
        name = "Category",
        description = "Endpoints for managing transaction categories (typed as EXPENSE or INCOME). A user only sees the categories they own plus the default ones, which have no owner."
)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Creates a category owned by the authenticated user, typed as EXPENSE or INCOME. The type is immutable after creation.")
    @ApiResponse(responseCode = "201", description = "Category created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<CategoryToApiViewDTO> createCategory(@AuthenticationPrincipal User user, @RequestBody @Valid CreateCategoryDTO body, UriComponentsBuilder uriComponentsBuilder) {
        var category = categoryService.createCategory(user, body);
        var uri = uriComponentsBuilder.path("/category/{id}").buildAndExpand(category.getId()).toUri();
        return ResponseEntity.created(uri).body(new CategoryToApiViewDTO(category));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update category", description = "Updates a category's name/description. The type cannot be changed, default categories cannot be edited, and categories owned by another user are not found.")
    @ApiResponse(responseCode = "200", description = "Category updated")
    @ApiResponse(responseCode = "403", description = "Default category cannot be edited")
    @ApiResponse(responseCode = "404", description = "Category not found or owned by another user")
    public ResponseEntity<CategoryToApiViewDTO> updateCategory(@AuthenticationPrincipal User user, @PathVariable String id, @RequestBody @Valid UpdateCategoryDTO body) {
        var category = categoryService.updateCategory(user, id, body);
        return ResponseEntity.ok(new CategoryToApiViewDTO(category));
    }


    @GetMapping
    @Operation(summary = "List categories", description = "Lists the authenticated user's categories plus the default ones, optionally filtered by type (?type=EXPENSE|INCOME).")
    @ApiResponse(responseCode = "200", description = "List of categories")
    public ResponseEntity<List<CategoryToApiViewDTO>> getCategories(@AuthenticationPrincipal User user, @RequestParam(required = false) TransactionType type) {
        var categories = categoryService.getAllCategories(user, type);
        return ResponseEntity.ok(CategoryToApiViewDTO.toList(categories));

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category", description = "Returns a category by id, as long as it belongs to the authenticated user or is a default one.")
    @ApiResponse(responseCode = "200", description = "Category data")
    @ApiResponse(responseCode = "404", description = "Category not found or owned by another user")
    public ResponseEntity<CategoryToApiViewDTO> getCategoryById(@AuthenticationPrincipal User user, @PathVariable String id) {
        var category = categoryService.getCategoryById(user, id);
        return ResponseEntity.ok(new CategoryToApiViewDTO(category));
    }
}

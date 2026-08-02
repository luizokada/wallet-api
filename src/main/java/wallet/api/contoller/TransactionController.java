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
import wallet.api.domain.transaction.dto.CreateTransactionDTO;
import wallet.api.domain.transaction.dto.TransactionToApiViewDTO;
import wallet.api.domain.transaction.dto.UpdateTransactionDTO;
import wallet.api.domain.transaction.service.TransactionService;
import wallet.api.domain.user.entity.User;

@RestController
@RequestMapping("transaction")
@Tag(
        name = "Transaction",
        description = "Endpoints for managing transactions (incomes and expenses)"
)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @Operation(summary = "Create transaction", description = "Creates an INCOME or EXPENSE transaction in the authenticated user's wallet, optionally linked to a category of the same type. Amount is in cents and must be positive.")
    @ApiResponse(responseCode = "201", description = "Transaction created")
    @ApiResponse(responseCode = "400", description = "Validation error or category type does not match transaction type")
    @ApiResponse(responseCode = "404", description = "User has no wallet or category not found")
    public ResponseEntity<TransactionToApiViewDTO> createTransaction(@AuthenticationPrincipal User user, @RequestBody @Valid CreateTransactionDTO body, UriComponentsBuilder uriBuild) {
        var createdTransaction = this.transactionService.createTransaction(user, body);

        var uri = uriBuild.path("/transaction/{id}").buildAndExpand(createdTransaction.getId()).toUri();

        CategoryToApiViewDTO categoryJson = null;
        if (createdTransaction.getCategory() != null) {
            categoryJson = new CategoryToApiViewDTO(createdTransaction.getCategory());
        }
        var jsonToReturn = new TransactionToApiViewDTO(createdTransaction, categoryJson);

        return ResponseEntity.created(uri).body(jsonToReturn);

    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update transaction", description = "Updates a transaction (type, amount, description, category, date). The resulting type/category pair is revalidated.")
    @ApiResponse(responseCode = "200", description = "Transaction updated")
    @ApiResponse(responseCode = "400", description = "Category type does not match transaction type")
    @ApiResponse(responseCode = "404", description = "Transaction or category not found")
    public ResponseEntity<TransactionToApiViewDTO> updateTransaction(@AuthenticationPrincipal User user, @PathVariable String id, @RequestBody @Valid UpdateTransactionDTO payload) {

        var updatedTransaction = this.transactionService.updateTransaction(id, user, payload);
        CategoryToApiViewDTO categoryJson = null;
        if (updatedTransaction.getCategory() != null) {
            categoryJson = new CategoryToApiViewDTO(updatedTransaction.getCategory());
        }
        var jsonToReturn = new TransactionToApiViewDTO(updatedTransaction, categoryJson);
        return ResponseEntity.ok().body(jsonToReturn);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction", description = "Deletes a transaction by id. Only the owner of the transaction's wallet can delete it.")
    @ApiResponse(responseCode = "200", description = "Transaction deleted")
    @ApiResponse(responseCode = "403", description = "Transaction belongs to another user")
    @ApiResponse(responseCode = "404", description = "Transaction not found")
    public ResponseEntity<Void> deleteTransaction(@AuthenticationPrincipal User user, @PathVariable String id) {
        transactionService.deleteTransaction(id, user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction", description = "Returns a transaction by id.")
    @ApiResponse(responseCode = "200", description = "Transaction data")
    @ApiResponse(responseCode = "404", description = "Transaction not found")
    public ResponseEntity<TransactionToApiViewDTO> getTransactionById(@PathVariable String id) {
        var transaction = transactionService.getTransaction(id);
        CategoryToApiViewDTO categoryJson = null;
        if (transaction.getCategory() != null) {
            categoryJson = new CategoryToApiViewDTO(transaction.getCategory());
        }
        var jsonToReturn = new TransactionToApiViewDTO(transaction, categoryJson);
        return ResponseEntity.ok().body(jsonToReturn);
    }
}

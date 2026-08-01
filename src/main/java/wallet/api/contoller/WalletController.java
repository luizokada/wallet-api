package wallet.api.contoller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.wallet.dto.GetWalletDTO;
import wallet.api.domain.wallet.dto.UpdateWalletDTO;
import wallet.api.domain.wallet.dto.WalletToApiViewDTO;
import wallet.api.domain.wallet.service.WalletService;

@RestController
@RequestMapping("wallet")
@Tag(
        name = "Wallet",
        description = "Wallet management endpoints"
)
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    @Operation(summary = "Create wallet", description = "Creates a wallet for the authenticated user. Each user can only have one wallet.")
    @ApiResponse(responseCode = "201", description = "Wallet created")
    @ApiResponse(responseCode = "400", description = "User already has a wallet")
    public ResponseEntity<Object> createWallet(@AuthenticationPrincipal User user, UriComponentsBuilder uriComponentsBuilder) {
        var createWallet = walletService.createWallet(user);
        var uri = uriComponentsBuilder.path("/wallet/{id}").buildAndExpand(createWallet.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/{id}")
    @Operation(summary = "Get wallet with expenses", description = "Returns the wallet and its expenses filtered by the period sent in the body.")
    @ApiResponse(responseCode = "200", description = "Wallet with expenses for the period")
    @ApiResponse(responseCode = "404", description = "Wallet not found")
    public ResponseEntity<Object> getWallet(@PathVariable String id, @RequestBody @Valid GetWalletDTO param) {
        var wallet = walletService.getWalletAndExpenses(id, param);
        return ResponseEntity.ok(wallet);
    }


    @PatchMapping("/{id}")
    @Operation(summary = "Update wallet", description = "Updates the wallet balance.")
    @ApiResponse(responseCode = "200", description = "Wallet updated")
    @ApiResponse(responseCode = "404", description = "Wallet not found")
    public ResponseEntity<Object> updateWallet( @PathVariable String id, @RequestBody @Valid UpdateWalletDTO param) {
        var wallet = walletService.updateWallet(id, param);


        return ResponseEntity.ok(new WalletToApiViewDTO(wallet));
    }


}

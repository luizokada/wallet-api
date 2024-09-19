package wallet.api.contoller;


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
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<Object> createWallet(@AuthenticationPrincipal User user, UriComponentsBuilder uriComponentsBuilder) {
        var createWallet = walletService.createWallet(user);
        var uri = uriComponentsBuilder.path("/wallet/{id}").buildAndExpand(createWallet.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> getWallet(@PathVariable String id, @RequestBody @Valid GetWalletDTO param) {
        var wallet = walletService.getWalletAndExpenses(id, param);
        return ResponseEntity.ok(wallet);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateWallet( @PathVariable String id, @RequestBody @Valid UpdateWalletDTO param) {
        var wallet = walletService.updateWallet(id, param);


        return ResponseEntity.ok(new WalletToApiViewDTO(wallet));
    }


}

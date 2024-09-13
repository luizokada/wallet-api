package wallet.api.contoller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.user.entity.User;
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
        var createWallet = walletService.createWallet(user.getId());
        var uri = uriComponentsBuilder.path("/wallet/{id}").buildAndExpand(createWallet.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }


}

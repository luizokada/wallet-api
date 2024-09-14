package wallet.api.domain.wallet.service;

import org.springframework.stereotype.Service;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.wallet.entity.Wallet;
import wallet.api.domain.wallet.repository.WalletRepository;
import wallet.api.errors.wallet.UserAlreadyHasWallet;

@Service
public class WalletService {


    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(User user) {

        var foundWallet = walletRepository.findByUserId(user.getId());

        if(foundWallet != null) {
            throw new UserAlreadyHasWallet();
        }

        var wallet = new Wallet(user);

        return walletRepository.save(wallet);
    }
}

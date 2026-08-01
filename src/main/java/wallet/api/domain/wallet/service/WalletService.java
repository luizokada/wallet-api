package wallet.api.domain.wallet.service;

import org.springframework.stereotype.Service;

import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.wallet.dto.GetWalletDTO;
import wallet.api.domain.wallet.dto.WalletWithTransactionsToAPIViewDTO;
import wallet.api.domain.wallet.entity.Wallet;
import wallet.api.domain.wallet.repository.WalletRepository;
import wallet.api.errors.wallet.UserAlreadyHasWallet;
import wallet.api.errors.wallet.WalletNotFound;

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

    public WalletWithTransactionsToAPIViewDTO getWalletAndTransactions(String walletId , GetWalletDTO getWalletDto) {

        var wallet = walletRepository.findById(walletId).orElse(null);

        if(wallet == null) {
            throw new WalletNotFound();
        }

        var transactions = walletRepository.findTransactionsByDate(walletId, getWalletDto.startDate(), getWalletDto.endDate());
        var balance = walletRepository.calculateBalance(walletId, TransactionType.INCOME);

        return new WalletWithTransactionsToAPIViewDTO(wallet, balance, transactions);
    }

}

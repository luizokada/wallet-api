package wallet.api.domain.wallet.service;

import org.springframework.stereotype.Service;
import wallet.api.domain.expense.dto.ExpenseToApiViewDto;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.wallet.dto.GetWalletDTO;
import wallet.api.domain.wallet.dto.UpdateWalletDTO;
import wallet.api.domain.wallet.dto.WalletWithExpenseToAPIViewDTO;
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

    public WalletWithExpenseToAPIViewDTO getWalletAndExpenses(String walletId , GetWalletDTO getWalletDto) {

        var wallet = walletRepository.findById(walletId).orElse(null);

        if(wallet == null) {
            throw new WalletNotFound();
        }

        var ex = walletRepository.findWalletAndExpensesByExpenseDate(walletId, getWalletDto.startDate(), getWalletDto.endDate());

        return new WalletWithExpenseToAPIViewDTO(wallet,ex);
    }

    public Wallet updateWallet(String walletId,UpdateWalletDTO payload) {
        var wallet = walletRepository.findById(walletId).orElse(null);

        if(wallet == null) {
            throw new WalletNotFound();
        }
        wallet.update(payload);
        return walletRepository.save(wallet);
    }
}

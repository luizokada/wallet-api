package wallet.api.domain.wallet.dto;

import wallet.api.domain.transaction.dto.TransactionToApiViewDTO;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.wallet.entity.Wallet;

import java.util.List;

public record WalletWithTransactionsToAPIViewDTO(
        String id,
        Long balance,
        List<TransactionToApiViewDTO> transactions
) {
    public WalletWithTransactionsToAPIViewDTO(Wallet wallet, Long balance, List<Transaction> transactions){
        this(wallet.getId(), balance, TransactionToApiViewDTO.fromList(transactions));
    }
}

package wallet.api.domain.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.wallet.entity.Wallet;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query(value = "SELECT wa FROM wallet wa WHERE wa.userId = ?1")
    Wallet findWalletByUserId(String userId);

    Transaction findTransactionById(String id);

}

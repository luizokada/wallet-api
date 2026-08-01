package wallet.api.domain.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.wallet.entity.Wallet;

import java.util.Date;
import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet,String> {

    public Wallet findByUserId(String userId);

    @Query("select t from Transaction t where t.walletId = ?1 and t.date between ?2 and ?3")
    public List<Transaction> findTransactionsByDate(String walletId, Date startDate, Date endDate);

    @Query("select coalesce(sum(case when t.type = ?2 then t.amount else -t.amount end), 0L) from Transaction t where t.walletId = ?1")
    public Long calculateBalance(String walletId, TransactionType incomeType);

}

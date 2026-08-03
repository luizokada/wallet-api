package wallet.api.domain.transaction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.wallet.entity.Wallet;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query(value = "SELECT wa FROM wallet wa WHERE wa.userId = ?1")
    Wallet findWalletByUserId(String userId);

    Transaction findTransactionById(String id);

    @Query("select t from Transaction t where t.walletId = ?1 and t.date between ?2 and ?3")
    List<Transaction> findByWalletIdAndDateBetween(String walletId, Date start, Date end);

    @Query(value = "select t from Transaction t left join fetch t.category where t.walletId = ?1 order by t.date desc, t.id desc",
            countQuery = "select count(t) from Transaction t where t.walletId = ?1")
    Page<Transaction> findPageByWalletId(String walletId, Pageable pageable);

}

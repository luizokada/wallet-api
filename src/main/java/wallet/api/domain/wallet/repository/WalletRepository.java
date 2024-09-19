package wallet.api.domain.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wallet.api.domain.expense.entity.Expense;
import wallet.api.domain.wallet.entity.Wallet;

import java.util.Date;
import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet,String> {

    public Wallet findByUserId(String userId);

    @Query("select ex from wallet wl inner join expense ex on wl.id = ex.walletId  left join expenseCategory ec on ex.categoryId= ec.id where wl.id = ?1 and ex.expense_date between ?2 and ?3")
    public List<Expense> findWalletAndExpensesByExpenseDate(String walletId, Date startDate, Date endDate);

}

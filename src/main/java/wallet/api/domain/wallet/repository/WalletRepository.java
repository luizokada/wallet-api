package wallet.api.domain.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.wallet.entity.Wallet;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet,String> {

    public Wallet findByUserId(String userId);

}

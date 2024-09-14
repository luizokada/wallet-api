package wallet.api.domain.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wallet.api.domain.wallet.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet,String> {

    public Wallet findByUserId(String userId);

}

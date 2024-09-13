package wallet.api.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.wallet.entity.Wallet;

import java.util.List;

public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
    List<User> findAllByDeletedAtIsNull();

    User findByIdAndDeletedAtIsNull(String id);


}

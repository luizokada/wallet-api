package wallet.api.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wallet.api.domain.user.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
    List<User> findAllByDeletedAtIsNull();


}

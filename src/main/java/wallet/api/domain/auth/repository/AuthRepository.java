package wallet.api.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import wallet.api.domain.user.entity.User;

public interface AuthRepository extends JpaRepository<User,String> {
    UserDetails findByEmail(String email);

}

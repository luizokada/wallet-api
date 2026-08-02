package wallet.api.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wallet.api.domain.auth.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    PasswordResetToken findByToken(String token);

    void deleteAllByUserId(String userId);

}

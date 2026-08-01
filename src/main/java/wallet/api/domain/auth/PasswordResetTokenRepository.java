package wallet.api.domain.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    PasswordResetToken findByToken(String token);

    void deleteAllByUserId(String userId);

}

package wallet.api.domain.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wallet.api.domain.user.entity.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Table(name = "password_reset_tokens")
@Entity(name = "PasswordResetToken")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    private String token;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "created_at")
    private Instant createdAt;

    public PasswordResetToken(User user, long expirationMinutes) {
        this.userId = user.getId();
        this.token = UUID.randomUUID().toString();
        this.expiresAt = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);
        this.createdAt = Instant.now();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isUsed() {
        return this.usedAt != null;
    }

    public void markUsed() {
        this.usedAt = Instant.now();
    }
}

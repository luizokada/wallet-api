package wallet.api.domain.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wallet.api.domain.auth.entity.PasswordResetToken;
import wallet.api.domain.auth.repository.PasswordResetTokenRepository;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.user.repository.UserRepository;
import wallet.api.errors.auth.InvalidResetTokenError;
import wallet.api.infra.mail.MailService;

@Service
public class PasswordRecoveryService {

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository tokenRepository;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.url}")
    private String resetUrl;

    @Value("${app.password-reset.expiration-minutes}")
    private long expirationMinutes;

    public PasswordRecoveryService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, MailService mailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    // Não lança erro para email desconhecido: a resposta é sempre genérica,
    // para não revelar quais emails existem na base
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return;
        }

        tokenRepository.deleteAllByUserId(user.getId());

        var resetToken = new PasswordResetToken(user, expirationMinutes);
        tokenRepository.save(resetToken);

        var resetLink = resetUrl + "?token=" + resetToken.getToken();
        mailService.sendPasswordResetEmail(email, resetLink, expirationMinutes);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken found = tokenRepository.findByToken(token);

        if (found == null || found.isUsed() || found.isExpired()) {
            throw new InvalidResetTokenError();
        }

        User user = userRepository.findById(found.getUserId()).orElse(null);
        if (user == null) {
            throw new InvalidResetTokenError();
        }

        user.changePassword(passwordEncoder.encode(newPassword));
        found.markUsed();

        userRepository.save(user);
        tokenRepository.save(found);
    }
}

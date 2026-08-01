package wallet.api.domain.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.user.repository.UserRepository;
import wallet.api.errors.auth.InvalidResetTokenError;
import wallet.api.infra.mail.MailService;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordRecoveryService passwordRecoveryService;

    private final User user = new User("user-id", "Test User", "user@example.com", "old-hash", null, null, null, null);

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(passwordRecoveryService, "resetUrl", "http://localhost:5173/reset-password");
        ReflectionTestUtils.setField(passwordRecoveryService, "expirationMinutes", 30L);
    }

    @Test
    void forgotShouldDoNothingForUnknownEmail() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(null);

        passwordRecoveryService.forgotPassword("ghost@example.com");

        verify(tokenRepository, never()).save(any());
        verify(mailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyLong());
    }

    @Test
    void forgotShouldInvalidateOldTokensAndSendEmailWithLink() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);

        passwordRecoveryService.forgotPassword("user@example.com");

        verify(tokenRepository).deleteAllByUserId("user-id");

        var captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(captor.capture());
        var savedToken = captor.getValue();

        verify(mailService).sendPasswordResetEmail(
                eq("user@example.com"),
                contains("?token=" + savedToken.getToken()),
                eq(30L));
    }

    @Test
    void resetShouldUpdatePasswordAndMarkTokenUsed() {
        var token = new PasswordResetToken(user, 30L);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("nova-senha")).thenReturn("encoded-nova-senha");

        passwordRecoveryService.resetPassword(token.getToken(), "nova-senha");

        assertEquals("encoded-nova-senha", user.getPassword());
        assertTrue(token.isUsed());
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    void resetShouldRejectUnknownToken() {
        when(tokenRepository.findByToken("nope")).thenReturn(null);

        assertThrows(InvalidResetTokenError.class,
                () -> passwordRecoveryService.resetPassword("nope", "x"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void resetShouldRejectExpiredToken() {
        var expired = new PasswordResetToken("id-1", "user-id", "tok-expired",
                Instant.now().minusSeconds(60), null, Instant.now().minusSeconds(3600));
        when(tokenRepository.findByToken("tok-expired")).thenReturn(expired);

        assertThrows(InvalidResetTokenError.class,
                () -> passwordRecoveryService.resetPassword("tok-expired", "x"));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetShouldRejectAlreadyUsedToken() {
        var used = new PasswordResetToken("id-2", "user-id", "tok-used",
                Instant.now().plusSeconds(600), Instant.now().minusSeconds(60), Instant.now().minusSeconds(3600));
        when(tokenRepository.findByToken("tok-used")).thenReturn(used);

        assertThrows(InvalidResetTokenError.class,
                () -> passwordRecoveryService.resetPassword("tok-used", "x"));

        verify(userRepository, never()).save(any());
    }
}

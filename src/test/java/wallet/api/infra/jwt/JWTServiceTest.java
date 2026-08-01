package wallet.api.infra.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import wallet.api.domain.user.entity.User;
import wallet.api.errors.auth.InvalidTokenError;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTServiceTest {

    private JWTService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JWTService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "test-secret");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", "7");
    }

    @Test
    void shouldCreateAndDecodeToken() {
        var user = new User("user-id", "Test User", "user@example.com", "hash", null, null, null, null);

        var token = jwtService.createToken(user);

        assertNotNull(token);
        assertEquals("user@example.com", jwtService.getDecodedToken(token));
    }

    @Test
    void shouldThrowForMalformedToken() {
        assertThrows(InvalidTokenError.class, () -> jwtService.getDecodedToken("not.a.token"));
    }

    @Test
    void shouldThrowForTokenSignedWithAnotherSecret() {
        var user = new User("user-id", "Test User", "user@example.com", "hash", null, null, null, null);
        var token = jwtService.createToken(user);

        ReflectionTestUtils.setField(jwtService, "jwtSecret", "another-secret");

        assertThrows(InvalidTokenError.class, () -> jwtService.getDecodedToken(token));
    }

    @Test
    void expirationDateShouldBeInTheFuture() {
        assertTrue(jwtService.generateExpDate().isAfter(Instant.now()));
    }
}

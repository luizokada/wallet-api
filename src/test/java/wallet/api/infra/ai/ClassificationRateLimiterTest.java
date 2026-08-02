package wallet.api.infra.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import wallet.api.errors.ai.ClassificationRateLimitedError;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassificationRateLimiterTest {

    private ClassificationRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new ClassificationRateLimiter();
        ReflectionTestUtils.setField(rateLimiter, "windowSeconds", 30L);
    }

    @Test
    void shouldAllowTheFirstCall() {
        assertDoesNotThrow(() -> rateLimiter.checkAndRegister("user-a"));
    }

    @Test
    void shouldBlockTheSecondCallInsideTheWindow() {
        rateLimiter.checkAndRegister("user-a");

        var thrown = assertThrows(ClassificationRateLimitedError.class,
                () -> rateLimiter.checkAndRegister("user-a"));

        assertTrue(thrown.getRetryAfterSeconds() > 0);
        assertTrue(thrown.getRetryAfterSeconds() <= 30);
    }

    @Test
    void shouldTrackUsersIndependently() {
        rateLimiter.checkAndRegister("user-a");

        assertDoesNotThrow(() -> rateLimiter.checkAndRegister("user-b"));
        assertThrows(ClassificationRateLimitedError.class, () -> rateLimiter.checkAndRegister("user-a"));
    }

    @Test
    void shouldAllowAgainOnceTheWindowHasPassed() {
        ReflectionTestUtils.setField(rateLimiter, "windowSeconds", 1L);
        rateLimiter.checkAndRegister("user-a");

        assertThrows(ClassificationRateLimitedError.class, () -> rateLimiter.checkAndRegister("user-a"));

        assertDoesNotThrow(() -> {
            Thread.sleep(1100);
            rateLimiter.checkAndRegister("user-a");
        });
    }

    @Test
    void shouldBeDisabledWhenWindowIsZero() {
        ReflectionTestUtils.setField(rateLimiter, "windowSeconds", 0L);

        assertDoesNotThrow(() -> {
            rateLimiter.checkAndRegister("user-a");
            rateLimiter.checkAndRegister("user-a");
            rateLimiter.checkAndRegister("user-a");
        });
    }
}

package wallet.api.infra.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wallet.api.errors.ai.ClassificationRateLimitedError;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClassificationRateLimiter {

    private static final int CLEANUP_THRESHOLD = 1000;

    private final Map<String, Instant> lastCallByUser = new ConcurrentHashMap<>();

    @Value("${gemini.rate-limit-seconds}")
    private long windowSeconds;

    public void checkAndRegister(String userId) {
        if (windowSeconds <= 0) {
            return;
        }

        var window = Duration.ofSeconds(windowSeconds);
        var now = Instant.now();

        evictExpired(now, window);

        var previousCall = lastCallByUser.putIfAbsent(userId, now);
        if (previousCall == null) {
            return;
        }

        var elapsed = Duration.between(previousCall, now);
        if (elapsed.compareTo(window) < 0) {
            throw new ClassificationRateLimitedError(Math.max(1, window.minus(elapsed).toSeconds()));
        }

        lastCallByUser.put(userId, now);
    }

    private void evictExpired(Instant now, Duration window) {
        if (lastCallByUser.size() < CLEANUP_THRESHOLD) {
            return;
        }
        lastCallByUser.entrySet()
                .removeIf(entry -> Duration.between(entry.getValue(), now).compareTo(window) >= 0);
    }
}

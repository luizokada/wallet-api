package wallet.api.errors.ai;

public class ClassificationRateLimitedError extends RuntimeException {

    private final long retryAfterSeconds;

    public ClassificationRateLimitedError(long retryAfterSeconds) {
        super("Classification is rate limited, retry in %d seconds".formatted(retryAfterSeconds));
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}

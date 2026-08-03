package wallet.api.errors.ai;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "Automatic classification is not configured")
public class ClassificationUnavailableError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClassificationUnavailableError() {
        super("Automatic classification is not configured");
    }
}

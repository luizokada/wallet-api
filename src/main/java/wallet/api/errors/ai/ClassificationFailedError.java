package wallet.api.errors.ai;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_GATEWAY, reason = "Automatic classification provider failed")
public class ClassificationFailedError extends RuntimeException {
    public ClassificationFailedError() {
        super("Automatic classification provider failed");
    }
}

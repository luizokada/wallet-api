package wallet.api.errors.storage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_GATEWAY, reason = "Failed to store file")
public class FileStorageError extends RuntimeException {
    public FileStorageError() {
        super("Failed to store file");
    }
}

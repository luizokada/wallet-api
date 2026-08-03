package wallet.api.errors.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Document already exists")
public class UserDocumentError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserDocumentError() {
        super("Document already exists");
    }
}

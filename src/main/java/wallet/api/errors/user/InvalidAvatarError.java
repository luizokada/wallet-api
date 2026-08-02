package wallet.api.errors.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Sem "reason" fixo: a mensagem muda conforme o motivo (tipo, tamanho, arquivo vazio)
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidAvatarError extends RuntimeException {
    public InvalidAvatarError(String message) {
        super(message);
    }
}

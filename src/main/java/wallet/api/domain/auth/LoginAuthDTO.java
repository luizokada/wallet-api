package wallet.api.domain.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LoginAuthDTO(
        @Email
        String email,
        @NotEmpty
        @NotBlank
        String password) {
}

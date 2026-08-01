package wallet.api.domain.auth;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDTO(
        @NotBlank
        String token,

        @NotBlank
        String password
) {
}

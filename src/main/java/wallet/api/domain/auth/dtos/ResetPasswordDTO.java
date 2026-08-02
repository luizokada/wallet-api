package wallet.api.domain.auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDTO(
        @NotBlank
        String token,

        @NotBlank
        String password
) {
}

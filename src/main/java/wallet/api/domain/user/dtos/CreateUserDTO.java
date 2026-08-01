package wallet.api.domain.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import wallet.api.infra.validation.CpfOrCnpj;

import java.time.LocalDate;

public record CreateUserDTO(
                @NotBlank @NotEmpty String name,
                @Email String email,
                @NotBlank String password,
                @NotBlank @CpfOrCnpj String document,
                LocalDate birthday) {
}

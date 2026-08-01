package wallet.api.domain.user.dtos;


import wallet.api.infra.validation.CpfOrCnpj;

import java.time.LocalDate;

public record UpdateUserDTO(
        String name,
        @CpfOrCnpj
        String document,
        LocalDate birthday) {

}

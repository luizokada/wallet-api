package wallet.api.infra.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wallet.api.domain.user.dtos.UpdateUserDTO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfOrCnpjTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private boolean isValid(String document) {
        return validator.validate(new UpdateUserDTO(null, document, null)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678900",          // CPF sem máscara
            "123.456.789-00",       // CPF com máscara
            "11222333000144",       // CNPJ sem máscara
            "11.222.333/0001-44"    // CNPJ com máscara
    })
    void shouldAcceptCpfAndCnpjFormats(String document) {
        assertTrue(isValid(document));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",                  // curto demais
            "123456789012",         // 12 dígitos (nem CPF nem CNPJ)
            "abc.def.ghi-jk",       // letras
            "123.456.789/00",       // máscara errada
            "12345678900 "          // espaço no fim
    })
    void shouldRejectInvalidFormats(String document) {
        assertFalse(isValid(document));
    }

    @Test
    void shouldAcceptNullOnUpdate() {
        assertTrue(isValid(null));
    }
}

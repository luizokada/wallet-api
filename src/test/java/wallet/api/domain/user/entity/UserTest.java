package wallet.api.domain.user.entity;

import org.junit.jupiter.api.Test;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private static final LocalDate BIRTHDAY = LocalDate.of(1995, 5, 10);

    private User newUser() {
        return new User(new CreateUserDTO("Test User", "user@example.com", "plain", "123.456.789-00", BIRTHDAY), "encoded-password");
    }

    @Test
    void constructorShouldUseProvidedEncodedPasswordAndCreateWallet() {
        var user = newUser();

        assertEquals("encoded-password", user.getPassword());
        assertEquals("user@example.com", user.getUsername());
        assertEquals("123.456.789-00", user.getDocument());
        assertEquals(BIRTHDAY, user.getBirthday());
        assertNotNull(user.getWallet());
    }

    @Test
    void authoritiesShouldContainRoleUser() {
        var user = newUser();

        assertTrue(user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void deleteUserShouldAnonymizeData() {
        var user = newUser();

        user.deleteUser();

        assertNotNull(user.getDeletedAt());
        assertNotEquals("user@example.com", user.getEmail());
        assertEquals("", user.getName());
        assertNull(user.getDocument());
        assertNull(user.getBirthday());
    }

    @Test
    void updateUserShouldIgnoreNullFields() {
        var user = newUser();

        user.updateUser(new UpdateUserDTO(null, null, null));

        assertEquals("Test User", user.getName());
        assertEquals("123.456.789-00", user.getDocument());
        assertEquals(BIRTHDAY, user.getBirthday());
    }

    @Test
    void updateUserShouldChangeDocumentAndBirthday() {
        var user = newUser();
        var newBirthday = LocalDate.of(1990, 1, 1);

        user.updateUser(new UpdateUserDTO(null, "11.222.333/0001-44", newBirthday));

        assertEquals("11.222.333/0001-44", user.getDocument());
        assertEquals(newBirthday, user.getBirthday());
        assertEquals("Test User", user.getName());
    }
}

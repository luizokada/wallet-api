package wallet.api.domain.user.entity;

import org.junit.jupiter.api.Test;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private User newUser() {
        return new User(new CreateUserDTO("Test User", "user@example.com", "plain"), "encoded-password");
    }

    @Test
    void constructorShouldUseProvidedEncodedPasswordAndCreateWallet() {
        var user = newUser();

        assertEquals("encoded-password", user.getPassword());
        assertEquals("user@example.com", user.getUsername());
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
    }

    @Test
    void updateUserShouldIgnoreNullName() {
        var user = newUser();

        user.updateUser(new UpdateUserDTO(null));

        assertEquals("Test User", user.getName());
    }
}

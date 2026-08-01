package wallet.api.domain.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.user.repository.UserRepository;
import wallet.api.errors.user.UserDocumentError;
import wallet.api.errors.user.UserEmailError;
import wallet.api.errors.user.UserNotFound;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserShouldStoreEncodedPassword() {
        var dto = new CreateUserDTO("Test User", "user@example.com", "plain-password", null, null);
        when(userRepository.findByEmail("user@example.com")).thenReturn(null);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var created = userService.createUser(dto);

        assertEquals("encoded-password", created.getPassword());
        assertEquals("user@example.com", created.getEmail());
        assertNotNull(created.getWallet());
    }

    @Test
    void createUserShouldRejectDuplicatedEmailBeforeHashing() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(new User());
        var dto = new CreateUserDTO("Test User", "user@example.com", "plain-password", null, null);

        assertThrows(UserEmailError.class, () -> userService.createUser(dto));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserShouldRejectDuplicatedDocument() {
        var dto = new CreateUserDTO("Test User", "user@example.com", "plain-password", "12345678900", null);
        when(userRepository.findByEmail("user@example.com")).thenReturn(null);
        when(userRepository.findByDocument("12345678900")).thenReturn(new User());

        assertThrows(UserDocumentError.class, () -> userService.createUser(dto));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserShouldRejectDocumentOwnedByAnotherUser() {
        var user = new User(new CreateUserDTO("Test User", "user@example.com", "x", null, null), "hash");
        var documentOwner = new User("other-id", "Other", "other@example.com", "hash", "12345678900", null, null, null);
        when(userRepository.findById("id-1")).thenReturn(Optional.of(user));
        when(userRepository.findByDocument("12345678900")).thenReturn(documentOwner);

        assertThrows(UserDocumentError.class,
                () -> userService.updateUser("id-1", new UpdateUserDTO(null, "12345678900", null)));

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserShouldAllowKeepingOwnDocument() {
        var user = new User("id-1", "Test User", "user@example.com", "hash", "12345678900", null, null, null);
        when(userRepository.findById("id-1")).thenReturn(Optional.of(user));
        when(userRepository.findByDocument("12345678900")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var updated = userService.updateUser("id-1", new UpdateUserDTO("New Name", "12345678900", null));

        assertEquals("New Name", updated.getName());
    }

    @Test
    void getUserByIdShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById("missing-id"));
    }

    @Test
    void updateUserShouldChangeOnlyTheName() {
        var user = new User(new CreateUserDTO("Old Name", "user@example.com", "x", null, null), "hash");
        when(userRepository.findById("id-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var updated = userService.updateUser("id-1", new UpdateUserDTO("New Name", null, null));

        assertEquals("New Name", updated.getName());
        assertEquals("user@example.com", updated.getEmail());
    }

    @Test
    void updateUserShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userService.updateUser("missing-id", new UpdateUserDTO("x", null, null)));
    }

    @Test
    void deleteUserShouldSoftDeleteAndPersist() {
        var user = new User(new CreateUserDTO("Test User", "user@example.com", "x", null, null), "hash");
        when(userRepository.findById("id-1")).thenReturn(Optional.of(user));

        userService.deleteUser("id-1");

        assertNotNull(user.getDeletedAt());
        verify(userRepository).save(user);
    }
}

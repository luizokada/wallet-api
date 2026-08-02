package wallet.api.domain.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import wallet.api.domain.auth.dtos.LoginAuthDTO;
import wallet.api.domain.user.entity.User;
import wallet.api.infra.jwt.JWTService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService tokenService;

    @InjectMocks
    private AuthService authService;

    private final User user = new User("user-id", "Test User", "user@example.com", "hash", null, null, null, null, null);

    @Test
    void loginShouldAuthenticateWithEmailAndPasswordAndReturnToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.createToken(user)).thenReturn("jwt-token");

        var view = authService.login(new LoginAuthDTO("user@example.com", "senha-secreta"));

        var captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("user@example.com", captor.getValue().getPrincipal());
        assertEquals("senha-secreta", captor.getValue().getCredentials());

        assertEquals("user-id", view.id());
        assertEquals("Test User", view.name());
        assertEquals("jwt-token", view.token());
    }

    @Test
    void loginShouldNotCreateTokenWhenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginAuthDTO("user@example.com", "senha-errada")));

        verify(tokenService, never()).createToken(any());
    }
}

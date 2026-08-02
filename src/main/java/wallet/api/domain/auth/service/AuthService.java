package wallet.api.domain.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import wallet.api.domain.auth.dtos.AuthToApiViewDTO;
import wallet.api.domain.auth.dtos.LoginAuthDTO;
import wallet.api.domain.user.entity.User;
import wallet.api.infra.jwt.JWTService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JWTService tokenService;

    public AuthService(AuthenticationManager authenticationManager, JWTService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public AuthToApiViewDTO login(LoginAuthDTO loginPayload) {
        var credentials = new UsernamePasswordAuthenticationToken(loginPayload.email(), loginPayload.password());
        var authentication = authenticationManager.authenticate(credentials);
        var loggedUser = (User) authentication.getPrincipal();

        return new AuthToApiViewDTO(loggedUser, tokenService.createToken(loggedUser));
    }
}

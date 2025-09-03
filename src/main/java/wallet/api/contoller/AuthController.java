package wallet.api.contoller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wallet.api.domain.auth.AuthTOAPIView;
import wallet.api.domain.auth.LoginAuthDTO;
import wallet.api.domain.user.entity.User;
import wallet.api.infra.jwt.JWTService;
import wallet.api.infra.security.annotations.PublicRoute;

@RestController
@RequestMapping("/login")
@Tag(
        name = "Authentication",
        description = "Endpoints for user authentication and JWT token generation"
)
public class AuthController {

    private final AuthenticationManager manager;

    private final JWTService tokenService;

    public AuthController(AuthenticationManager manager, JWTService tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }


    @PostMapping
    @PublicRoute
    public ResponseEntity<AuthTOAPIView> login(@RequestBody @Valid LoginAuthDTO loginAuthDTO) {
        var token = new UsernamePasswordAuthenticationToken(loginAuthDTO.email(), loginAuthDTO.password());
        var auth = manager.authenticate(token);
        var logedUser = (User) auth.getPrincipal();
        var jwtToken = tokenService.createToken(logedUser);
        return ResponseEntity.ok(new AuthTOAPIView(logedUser.getId(), logedUser.getName(),jwtToken));
    }
}

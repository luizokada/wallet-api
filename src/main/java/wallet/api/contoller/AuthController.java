package wallet.api.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wallet.api.domain.auth.dtos.AuthToApiViewDTO;
import wallet.api.domain.auth.dtos.LoginAuthDTO;
import wallet.api.domain.auth.service.AuthService;
import wallet.api.infra.security.annotations.PublicRoute;

@RestController
@RequestMapping("/login")
@Tag(name = "Authentication", description = "Endpoints for user authentication and JWT token generation")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    @PublicRoute
    @Operation(summary = "Login", description = "Authenticates the user with email and password and returns a JWT token.")
    @ApiResponse(responseCode = "200", description = "Authenticated, returns user id, name and JWT token")
    @ApiResponse(responseCode = "401", description = "Invalid email or password")
    public ResponseEntity<AuthToApiViewDTO> login(@RequestBody @Valid LoginAuthDTO loginAuthDTO) {
        return ResponseEntity.ok(authService.login(loginAuthDTO));
    }
}

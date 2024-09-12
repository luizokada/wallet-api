package wallet.api.contoller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/login")
public class AuthController {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JWTService tokenService;


    @PostMapping
    public ResponseEntity<AuthTOAPIView> login(@RequestBody @Valid LoginAuthDTO loginAuthDTO) {
        var token = new UsernamePasswordAuthenticationToken(loginAuthDTO.email(), loginAuthDTO.password());
        var auth = manager.authenticate(token);
        var logedUser = (User) auth.getPrincipal();
        var jwtToken = tokenService.createToken(logedUser);
        return ResponseEntity.ok(new AuthTOAPIView(logedUser.getId(), logedUser.getName(),jwtToken));
    }
}

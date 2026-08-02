package wallet.api.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wallet.api.domain.auth.dtos.ForgotPasswordDTO;
import wallet.api.domain.auth.dtos.MessageToApiViewDTO;
import wallet.api.domain.auth.dtos.ResetPasswordDTO;
import wallet.api.domain.auth.service.PasswordRecoveryService;
import wallet.api.infra.security.annotations.PublicRoute;

@RestController
@RequestMapping("password")
@Tag(name = "Password Recovery", description = "Endpoints for recovering a forgotten password via email")
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService) {
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @PostMapping("/forgot")
    @PublicRoute
    @Transactional
    @Operation(summary = "Request password reset", description = "Sends a password reset link to the given email. Always returns 200 with a generic message, whether the email exists or not.")
    @ApiResponse(responseCode = "200", description = "Generic confirmation (does not reveal if the email exists)")
    @ApiResponse(responseCode = "400", description = "Invalid email format")
    public ResponseEntity<MessageToApiViewDTO> forgotPassword(@RequestBody @Valid ForgotPasswordDTO body) {
        passwordRecoveryService.forgotPassword(body.email());
        return ResponseEntity.ok(new MessageToApiViewDTO("If the email exists, a recovery link was sent"));
    }

    @PostMapping("/reset")
    @PublicRoute
    @Transactional
    @Operation(summary = "Reset password", description = "Sets a new password using the token received by email. The token is single-use and expires in 30 minutes.")
    @ApiResponse(responseCode = "200", description = "Password updated")
    @ApiResponse(responseCode = "400", description = "Invalid, used or expired token")
    public ResponseEntity<MessageToApiViewDTO> resetPassword(@RequestBody @Valid ResetPasswordDTO body) {
        passwordRecoveryService.resetPassword(body.token(), body.password());
        return ResponseEntity.ok(new MessageToApiViewDTO("Password updated"));
    }
}

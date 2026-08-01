package wallet.api.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;
import wallet.api.domain.user.dtos.UserToApiViewDTO;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.user.service.UserService;
import wallet.api.errors.user.NotResourceOwnerError;
import wallet.api.infra.security.annotations.PublicRoute;

import java.util.List;

@RestController
@RequestMapping("user")
@Tag(name = "user", description = "User endpoints")

public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create-user")
    @Transactional
    @PublicRoute
    @Operation(summary = "Create user", description = "Creates a new user with a BCrypt-hashed password and an empty wallet.")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "400", description = "Validation error or email already exists")
    public ResponseEntity<UserToApiViewDTO> createUser(@Valid @RequestBody CreateUserDTO userPayload, UriComponentsBuilder uriComponentsBuilder){


        User createdUser = userService.createUser(userPayload);

        var uri = uriComponentsBuilder.path("/user/{id}").buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserToApiViewDTO(createdUser)) ;
    }

    @GetMapping("/me")
    @Operation(summary = "Get logged user", description = "Returns the data of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Logged user data")
    public ResponseEntity<UserToApiViewDTO> getUser(@AuthenticationPrincipal User user){
        var foundUser = userService.getUserById(user.getId());
        return ResponseEntity.ok().body(new UserToApiViewDTO(foundUser)) ;
    }

    @PatchMapping("/{id}")
    @Transactional
    @Operation(summary = "Update user", description = "Updates the user's name. Only the owner of the account can update it.")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "403", description = "Trying to update another user's account")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserToApiViewDTO> updateUser(@PathVariable String id, @Valid @RequestBody UpdateUserDTO userPayload, @AuthenticationPrincipal User loggedUser){
        if(!loggedUser.getId().equals(id)){
            throw new NotResourceOwnerError();
        }
        User updatedUser = userService.updateUser(id, userPayload);
        return ResponseEntity.ok().body(new UserToApiViewDTO(updatedUser)) ;
    }

    @GetMapping
    @Operation(summary = "List users", description = "Lists all non-deleted users.")
    @ApiResponse(responseCode = "200", description = "List of users")
    public ResponseEntity<List<UserToApiViewDTO>> getAllUsers(){
        List<User> users = userService.listUser();
        return ResponseEntity.ok().body(UserToApiViewDTO.toList(users)) ;
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Delete user", description = "Soft-deletes the user (anonymizes data and sets deleted_at). Only the owner of the account can delete it.")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "403", description = "Trying to delete another user's account")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Object> deleteUser(@PathVariable String id, @AuthenticationPrincipal User loggedUser){
        if(!loggedUser.getId().equals(id)){
            throw new NotResourceOwnerError();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}

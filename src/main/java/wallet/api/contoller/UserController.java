package wallet.api.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;
import wallet.api.domain.user.dtos.UserToApiViewDTO;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.user.service.UserService;
import wallet.api.errors.user.NotResourceOwnerError;
import wallet.api.infra.security.annotations.PublicRoute;

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
        return ResponseEntity.created(uri).body(userService.toApiView(createdUser)) ;
    }

    @GetMapping("/me")
    @Operation(summary = "Get logged user", description = "Returns the data of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Logged user data")
    public ResponseEntity<UserToApiViewDTO> getUser(@AuthenticationPrincipal User user){
        var foundUser = userService.getUserById(user.getId());
        return ResponseEntity.ok().body(userService.toApiView(foundUser)) ;
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
        return ResponseEntity.ok().body(userService.toApiView(updatedUser)) ;
    }

    // Sem @Transactional: o upload é uma chamada externa (Supabase) e não vale
    // segurar conexão do banco aberta esperando a rede
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload user avatar", description = "Uploads the profile picture to the storage bucket and saves its path. The response returns the full public URL. Only the owner of the account can do it.")
    @ApiResponse(responseCode = "200", description = "Avatar updated")
    @ApiResponse(responseCode = "400", description = "Missing file, unsupported type or file too large")
    @ApiResponse(responseCode = "403", description = "Trying to update another user's avatar")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "502", description = "Storage provider failure")
    public ResponseEntity<UserToApiViewDTO> uploadAvatar(@PathVariable String id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal User loggedUser){
        if(!loggedUser.getId().equals(id)){
            throw new NotResourceOwnerError();
        }
        User updatedUser = userService.updateAvatar(id, file);
        return ResponseEntity.ok().body(userService.toApiView(updatedUser)) ;
    }

    @DeleteMapping("/{id}/avatar")
    @Operation(summary = "Remove user avatar", description = "Removes the profile picture from the storage bucket and clears its path. Only the owner of the account can do it.")
    @ApiResponse(responseCode = "200", description = "Avatar removed")
    @ApiResponse(responseCode = "403", description = "Trying to remove another user's avatar")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserToApiViewDTO> deleteAvatar(@PathVariable String id, @AuthenticationPrincipal User loggedUser){
        if(!loggedUser.getId().equals(id)){
            throw new NotResourceOwnerError();
        }
        User updatedUser = userService.deleteAvatar(id);
        return ResponseEntity.ok().body(userService.toApiView(updatedUser)) ;
    }

    // Rota desativada: listar todos os usuários expõe dados pessoais (email, document, birthday)
    // e só faria sentido para admins — sem plano de roles/admin por enquanto.
    // @GetMapping
    // @Operation(summary = "List users", description = "Lists all non-deleted users.")
    // @ApiResponse(responseCode = "200", description = "List of users")
    // public ResponseEntity<List<UserToApiViewDTO>> getAllUsers(){
    //     List<User> users = userService.listUser();
    //     return ResponseEntity.ok().body(userService.toApiViewList(users)) ;
    // }

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

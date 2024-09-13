package wallet.api.contoller;

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
import wallet.api.domain.user.repository.UserRepository;
import wallet.api.domain.user.service.UserService;
import wallet.api.errors.user.UserEmailError;
import wallet.api.errors.user.UserNotFound;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create-user")
    @Transactional
    public ResponseEntity<UserToApiViewDTO> createUser(@Valid @RequestBody CreateUserDTO userPayload, UriComponentsBuilder uriComponentsBuilder){


        User createdUser = userService.createUser(userPayload);

        var uri = uriComponentsBuilder.path("/user/{id}").buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserToApiViewDTO(createdUser)) ;
    }

    @GetMapping("/me")
    public ResponseEntity<UserToApiViewDTO> getUser(@AuthenticationPrincipal User user){
        var foundUser = userService.getUserById(user.getId());
        return ResponseEntity.ok().body(new UserToApiViewDTO(foundUser)) ;
    }

    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<UserToApiViewDTO> updateUser(@PathVariable String id, @Valid @RequestBody UpdateUserDTO userPayload){

        User updatedUser = userService.updateUser(id, userPayload);
        return ResponseEntity.ok().body(new UserToApiViewDTO(updatedUser)) ;
    }

    @GetMapping
    public ResponseEntity<List<UserToApiViewDTO>> getAllUsers(){
        List<User> users = userService.listUser();
        return ResponseEntity.ok().body(UserToApiViewDTO.toList(users)) ;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> deleteUser(@PathVariable String id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}

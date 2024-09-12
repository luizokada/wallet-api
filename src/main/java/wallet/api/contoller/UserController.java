package wallet.api.contoller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import wallet.api.domain.user.*;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;
import wallet.api.domain.user.dtos.UserToApiViewDTO;
import wallet.api.domain.user.entity.User;
import wallet.api.errors.user.UserEmailError;
import wallet.api.errors.user.UserNotFound;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UserToApiViewDTO> createUser(@Valid @RequestBody CreateUserDTO userPayload, UriComponentsBuilder uriComponentsBuilder){
        User user = new User(userPayload);

        User founduser = userRepository.findByEmail(user.getEmail());
        if(founduser!=null){
            throw new UserEmailError();
        }
        User createdUser = userRepository.save(user);

        var uri = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserToApiViewDTO(createdUser)) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserToApiViewDTO> getUser(@PathVariable("id")  String id){
        User foundUser = userRepository.findByIdAndDeletedAtIsNull(id);
        if(foundUser==null){
            throw new UserNotFound();
        }

        return ResponseEntity.ok().body(new UserToApiViewDTO(foundUser)) ;
    }

    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<UserToApiViewDTO> updateUser(@PathVariable String id, @Valid @RequestBody UpdateUserDTO userPayload){
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        foundUser.updateUser(userPayload);
        User updatedUser = userRepository.save(foundUser);
        return ResponseEntity.ok().body(new UserToApiViewDTO(updatedUser)) ;
    }

    @GetMapping
    public ResponseEntity<List<UserToApiViewDTO>> getAllUsers(){
        List<User> users = userRepository.findAllByDeletedAtIsNull();
        return ResponseEntity.ok().body(UserToApiViewDTO.toList(users)) ;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> deleteUser(@PathVariable String id){
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        foundUser.deleteUser();
        userRepository.save(foundUser);
        return ResponseEntity.noContent().build();
    }

}

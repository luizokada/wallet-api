package wallet.api.contoller;

import jakarta.validation.Valid;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import wallet.api.errors.user.UserEmailError;
import wallet.api.errors.user.UserNotFound;
import wallet.api.user.*;

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
    public UserToApiViewDTO createUser(@Valid @RequestBody CreateUserDTO userPayload ){
        User user = new User(userPayload);

        User founduser = userRepository.findByEmail(user.getEmail());
        if(founduser!=null){
            throw new UserEmailError();
        }
        User createdUser = userRepository.save(user);
        return new UserToApiViewDTO(createdUser);
    }

    @GetMapping("/{id}")
    public UserToApiViewDTO getUser(@PathVariable("id")  String id){
        User foundUser = userRepository.findByIdAndDeletedAtIsNull(id);
        if(foundUser==null){
            throw new UserNotFound();
        }
        return new UserToApiViewDTO(foundUser);
    }

    @PatchMapping("/{id}")
    @Transactional
    public UserToApiViewDTO updateUser(@PathVariable String id, @Valid @RequestBody UpdateUserDTO userPayload){
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        foundUser.updateUser(userPayload);
        User updatedUser = userRepository.save(foundUser);
        return new UserToApiViewDTO(updatedUser);
    }

    @GetMapping
    public List<UserToApiViewDTO> getAllUsers(){
        List<User> users = userRepository.findAllByDeletedAtIsNull();
        return UserToApiViewDTO.toList(users);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void deleteUser(@PathVariable String id){
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        foundUser.deleteUser();
        userRepository.save(foundUser);
    }

}

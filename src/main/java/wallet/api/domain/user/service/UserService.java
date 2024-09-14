package wallet.api.domain.user.service;

import org.springframework.stereotype.Service;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.user.repository.UserRepository;
import wallet.api.errors.user.UserEmailError;
import wallet.api.errors.user.UserNotFound;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public User getUserById(String id) {
        var founduser =  userRepository.findById(id).orElse(null);
        if(founduser==null) {
            throw new RuntimeException("User not found");
        }
        return founduser;
    }

    public User createUser(CreateUserDTO userPayload) {
        User user = new User(userPayload);

        User founduser = userRepository.findByEmail(user.getEmail());
        if(founduser!=null){
            throw new UserEmailError();
        }
        var createdUser =  userRepository.save(user);
        return createdUser;
    }

    public User updateUser(String id, UpdateUserDTO userPayload) {
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        foundUser.updateUser(userPayload);
        return userRepository.save(foundUser);
    }

    public void deleteUser(String id) {
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        foundUser.deleteUser();
        userRepository.save(foundUser);
    }

    public List<User> listUser(){
        return userRepository.findAllByDeletedAtIsNull();
    }


}

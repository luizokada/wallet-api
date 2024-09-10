package wallet.api.contoller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wallet.api.user.CreateUserDTO;
import wallet.api.user.User;

@RestController
@RequestMapping("user")
public class UserController {

    @PostMapping

    public String createUser(@RequestBody CreateUserDTO userPayload ){
        User user = new User(userPayload);
        System.out.println(user.getPassword());
        return "User created";
    }
}

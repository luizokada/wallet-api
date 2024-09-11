package wallet.api.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

@Table(name = "users")
@Entity(name = "User")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String email;
    private String password;
    private Date deletedAt;

    public User(CreateUserDTO createUserDTO) {
        this.name = createUserDTO.name();
        this.email = createUserDTO.email();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        this.password = encoder.encode(createUserDTO.password());

    }

    public void updateUser(UpdateUserDTO userPayload){
        if (userPayload.name() != null){
            this.name = userPayload.name();
        }

    }

    public void deleteUser(){
        this.name = "";
        this.email = new Date().toString();
        this.password =  new Date().toString();
        this.deletedAt = new Date();
    }

}

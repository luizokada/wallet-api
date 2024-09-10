package wallet.api.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    public User(CreateUserDTO createUserDTO) {
        this.name = createUserDTO.name();
        this.email = createUserDTO.email();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        this.password = encoder.encode(createUserDTO.password());

    }

}

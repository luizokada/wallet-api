package wallet.api.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;
import wallet.api.domain.wallet.entity.Wallet;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Table(name = "users")
@Entity(name = "User")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String email;
    private String password;
    private Date deletedAt;

    @OneToOne(mappedBy = "user",cascade = CascadeType.PERSIST)
    private Wallet wallet;

    public User(CreateUserDTO createUserDTO) {
        this.name = createUserDTO.name();
        this.email = createUserDTO.email();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        this.password = encoder.encode(createUserDTO.password());
        this.wallet = new Wallet(this);

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

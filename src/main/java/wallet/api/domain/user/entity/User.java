package wallet.api.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;
import wallet.api.domain.wallet.entity.Wallet;

import java.time.LocalDate;
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
    private String document;
    private LocalDate birthday;
    // Caminho do objeto no bucket, sem o domínio do storage
    private String avatarPath;
    private Date deletedAt;

    @OneToOne(mappedBy = "user",cascade = CascadeType.PERSIST)
    private Wallet wallet;

    public User(CreateUserDTO createUserDTO, String encodedPassword) {
        this.name = createUserDTO.name();
        this.email = createUserDTO.email();
        this.password = encodedPassword;
        this.document = createUserDTO.document();
        this.birthday = createUserDTO.birthday();
        this.wallet = new Wallet(this);

    }

    public void updateUser(UpdateUserDTO userPayload){
        if (userPayload.name() != null){
            this.name = userPayload.name();
        }
        if (userPayload.document() != null){
            this.document = userPayload.document();
        }
        if (userPayload.birthday() != null){
            this.birthday = userPayload.birthday();
        }

    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeAvatar(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void deleteUser(){
        this.name = "";
        this.email = new Date().toString();
        this.password =  new Date().toString();
        this.document = null;
        this.birthday = null;
        this.avatarPath = null;
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

    public String getWalletId(){
        if(this.wallet == null){
            return null;
        }
        return this.wallet.getId();
    }
}

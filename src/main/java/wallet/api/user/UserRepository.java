package wallet.api.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
    List<User> findAllByDeletedAtIsNull();
    User findByIdAndDeletedAtIsNull(String id);
}

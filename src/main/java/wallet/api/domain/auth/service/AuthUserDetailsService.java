package wallet.api.domain.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import wallet.api.domain.auth.repository.AuthRepository;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final AuthRepository authRepository;

    public AuthUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return authRepository.findByEmail(username);
    }

}

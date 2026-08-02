package wallet.api.domain.auth.dtos;

import wallet.api.domain.user.entity.User;

public record AuthToApiViewDTO(
        String id,
        String name,
        String token
) {
    public AuthToApiViewDTO(User user, String token) {
        this(user.getId(), user.getName(), token);
    }
}

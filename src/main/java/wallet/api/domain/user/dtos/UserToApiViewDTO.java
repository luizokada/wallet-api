package wallet.api.domain.user.dtos;

import wallet.api.domain.user.entity.User;

import java.time.LocalDate;

// O banco guarda só o path do avatar; a URL completa é montada por quem responde
// (UserService.toApiView) para o front não precisar saber onde o arquivo mora
public record UserToApiViewDTO(String id, String email, String name, String document, LocalDate birthday, String avatarUrl, String walletId) {
    public UserToApiViewDTO(User user, String avatarUrl){
        this(user.getId(), user.getEmail(), user.getName(), user.getDocument(), user.getBirthday(), avatarUrl, user.getWalletId());
    }
}

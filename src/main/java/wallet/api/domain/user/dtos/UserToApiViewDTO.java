package wallet.api.domain.user.dtos;

import wallet.api.domain.user.entity.User;

import java.util.List;

public record UserToApiViewDTO(String id, String email, String name) {
    public UserToApiViewDTO(User user){
        this(user.getId(), user.getEmail(), user.getName());
    }
    public static List<UserToApiViewDTO> toList(List<User> users) {
        return users.stream()
                .map(UserToApiViewDTO::new)
                .toList();
    }
}

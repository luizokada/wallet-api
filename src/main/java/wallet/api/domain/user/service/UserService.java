package wallet.api.domain.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import wallet.api.domain.user.dtos.CreateUserDTO;
import wallet.api.domain.user.dtos.UpdateUserDTO;
import wallet.api.domain.user.dtos.UserToApiViewDTO;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.user.repository.UserRepository;
import wallet.api.errors.storage.FileStorageError;
import wallet.api.errors.user.InvalidAvatarError;
import wallet.api.errors.user.UserDocumentError;
import wallet.api.errors.user.UserEmailError;
import wallet.api.errors.user.UserNotFound;
import wallet.api.infra.storage.StorageService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    // A extensão vem do content-type e não do nome do arquivo enviado pelo cliente
    private static final Map<String, String> ALLOWED_AVATAR_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final StorageService storageService;

    @Value("${app.avatar.max-size-bytes:2097152}")
    private long avatarMaxSizeBytes;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, StorageService storageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.storageService = storageService;

    }

    public User getUserById(String id) {
        var founduser =  userRepository.findById(id).orElse(null);
        if(founduser==null) {
            throw new RuntimeException("User not found");
        }
        return founduser;
    }

    public User createUser(CreateUserDTO userPayload) {
        User founduser = userRepository.findByEmail(userPayload.email());
        if (founduser != null) {
            throw new UserEmailError();
        }
        if (userPayload.document() != null && userRepository.findByDocument(userPayload.document()) != null) {
            throw new UserDocumentError();
        }
        User user = new User(userPayload, passwordEncoder.encode(userPayload.password()));
        var createdUser =  userRepository.save(user);
        return createdUser;
    }

    public User updateUser(String id, UpdateUserDTO userPayload) {
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        if (userPayload.document() != null) {
            User documentOwner = userRepository.findByDocument(userPayload.document());
            if (documentOwner != null && !id.equals(documentOwner.getId())) {
                throw new UserDocumentError();
            }
        }
        foundUser.updateUser(userPayload);
        return userRepository.save(foundUser);
    }

    public User updateAvatar(String id, MultipartFile file) {
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        validateAvatar(file);

        var extension = ALLOWED_AVATAR_TYPES.get(file.getContentType());
        // UUID no nome para o navegador/CDN não continuar servindo a foto antiga do cache
        var path = "avatars/%s/%s.%s".formatted(id, UUID.randomUUID(), extension);

        try {
            storageService.upload(path, file.getBytes(), file.getContentType());
        } catch (IOException e) {
            throw new FileStorageError();
        }

        var previousAvatarPath = foundUser.getAvatarPath();
        foundUser.changeAvatar(path);
        var savedUser = userRepository.save(foundUser);

        if (previousAvatarPath != null) {
            storageService.delete(previousAvatarPath);
        }
        return savedUser;
    }

    public User deleteAvatar(String id) {
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        var previousAvatarPath = foundUser.getAvatarPath();
        foundUser.changeAvatar(null);
        var savedUser = userRepository.save(foundUser);

        if (previousAvatarPath != null) {
            storageService.delete(previousAvatarPath);
        }
        return savedUser;
    }

    // O path vira URL pública só aqui, na saída
    public UserToApiViewDTO toApiView(User user) {
        return new UserToApiViewDTO(user, storageService.buildPublicUrl(user.getAvatarPath()));
    }

    public List<UserToApiViewDTO> toApiViewList(List<User> users) {
        return users.stream().map(this::toApiView).toList();
    }

    private void validateAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidAvatarError("Avatar file is required");
        }
        if (!ALLOWED_AVATAR_TYPES.containsKey(file.getContentType())) {
            throw new InvalidAvatarError("Avatar must be a JPEG, PNG or WEBP image");
        }
        if (file.getSize() > avatarMaxSizeBytes) {
            throw new InvalidAvatarError("Avatar must be smaller than %d MB".formatted(avatarMaxSizeBytes / (1024 * 1024)));
        }
    }

    public void deleteUser(String id) {
        User foundUser = userRepository.findById(id).orElse(null);
        if(foundUser==null){
            throw new UserNotFound();
        }
        var previousAvatarPath = foundUser.getAvatarPath();
        foundUser.deleteUser();
        userRepository.save(foundUser);

        if (previousAvatarPath != null) {
            storageService.delete(previousAvatarPath);
        }
    }

    public List<User> listUser(){
        return userRepository.findAllByDeletedAtIsNull();
    }


}

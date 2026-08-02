package wallet.api.infra.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import wallet.api.errors.storage.FileStorageError;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    private final S3Client s3Client;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    // Base pública do bucket, terminando em /object/public/
    @Value("${supabase.storage.public-url}")
    private String publicUrl;

    public StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void upload(String key, byte[] content, String contentType) {
        try {
            var request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(content));
            log.info("File uploaded to {}/{}", bucket, key);
        } catch (S3Exception e) {
            log.error("Failed to upload {}/{}: {}", bucket, key, e.getMessage());
            throw new FileStorageError();
        }
    }

    // Remoção é best-effort: se o arquivo antigo não sair do bucket, o usuário
    // já está com o avatar novo e não faz sentido derrubar a requisição por isso
    public void delete(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
            log.info("File deleted from {}/{}", bucket, key);
        } catch (S3Exception e) {
            log.error("Failed to delete {}/{}: {}", bucket, key, e.getMessage());
        }
    }

    // O banco guarda só a key; a URL pública é montada na hora de responder
    public String buildPublicUrl(String key) {
        if (key == null) {
            return null;
        }
        return publicUrl + bucket + "/" + key;
    }
}

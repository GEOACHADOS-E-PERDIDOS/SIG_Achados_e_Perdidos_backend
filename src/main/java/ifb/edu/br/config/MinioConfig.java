package ifb.edu.br.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.minio.MinioClient;

@Configuration
public class MinioConfig {

    @Value("${minio.access.key}")
    private String acessKey;

    @Value("${minio.acess.secret}")
    private String secretKey;
    
    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Bean
    @Primary
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(minioUrl)
            .credentials(acessKey,secretKey)
            .build();
    }
}
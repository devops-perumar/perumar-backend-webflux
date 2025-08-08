// src/main/java/pe/edu/perumar/perumar_backend/service/S3SignedUrlService.java

package pe.edu.perumar.perumar_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class S3SignedUrlService {

    private final String bucketName;
    private final Region region;

    public S3SignedUrlService(@Value("${aws.s3.bucket}") String bucketName,
                              @Value("${aws.region}") String region) {
        this.bucketName = bucketName;
        this.region = Region.of(region);
    }

    public String getSignedUrl(String objectKey, int expiresInSeconds) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofSeconds(expiresInSeconds))
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        }
    }
}

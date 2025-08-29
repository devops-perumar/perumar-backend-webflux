package pe.edu.perumar.perumar_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@Profile("!test")
public class DynamoDBConfig {

    /**
     * Cliente asincrónico base de DynamoDB (para DynamoDbEnhancedAsyncClient)
     */
    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient() {
        return DynamoDbAsyncClient.builder()
                .region(resolveRegion())
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Cliente asincrónico mejorado de DynamoDB (para uso con repositorios)
     */
    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(DynamoDbAsyncClient base) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(base)
                .build();
    }

    /**
     * Cliente sincrónico (por si algún servicio lo requiere)
     */
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(resolveRegion())
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Resuelve la región desde propiedad del sistema o usa us-east-1 como default
     */
    private Region resolveRegion() {
        String region = System.getProperty("aws.region");
        return Region.of(region != null ? region : "us-east-1");
    }
}

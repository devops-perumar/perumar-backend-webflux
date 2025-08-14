package pe.edu.perumar.perumar_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Configuration
@Profile("!test")
public class DynamoDBConfig {

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient() {
        return DynamoDbAsyncClient.builder()
                .region(Region.of(System.getProperty("aws.region", "us-east-1"))) // Si quieres configurable por properties
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

  @Bean
  public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(DynamoDbAsyncClient base) {
    return DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(base)
        .build();
  }
}

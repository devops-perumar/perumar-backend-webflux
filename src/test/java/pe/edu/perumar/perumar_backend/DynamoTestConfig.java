package pe.edu.perumar.perumar_backend;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@TestConfiguration
@Profile("test")
public class DynamoTestConfig {

  @Bean
  @Primary
  DynamoDbAsyncClient dynamoDbAsyncClient() {
    return Mockito.mock(DynamoDbAsyncClient.class);
  }

  @Bean
  @Primary
  DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(DynamoDbAsyncClient dynamoDbAsyncClient) {
    return DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(dynamoDbAsyncClient)
        .build();
  }
}

package pe.edu.perumar.perumar_backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class DynamoTestConfig {

    @Bean
    public DynamoDbClient dynamoDbClient() {
        // Mock para no usar AWS real durante los tests
        return mock(DynamoDbClient.class);
    }
}

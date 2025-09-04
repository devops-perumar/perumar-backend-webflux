package pe.edu.perumar.perumar_backend.acl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Repository
public class AclDynamoRepositoryImpl implements AclDynamoRepository {

    private final DynamoDbClient dynamoDbClient;

    @Value("${acl.dynamodb.table-name}")
    private String tableName;

    public AclDynamoRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public Mono<Boolean> hasAccess(String role, String resource, String action, String scope) {
        return Mono.fromCallable(() -> {
            String resourceKey = String.format("%s#%s#%s", resource, action, scope);

            Map<String, AttributeValue> key = new HashMap<>();
            key.put("role", AttributeValue.builder().s(role).build());
            key.put("resource_action_scope", AttributeValue.builder().s(resourceKey).build());

            GetItemRequest request = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .attributesToGet("allow")
                    .consistentRead(true)
                    .build();

            GetItemResponse response = dynamoDbClient.getItem(request);

            if (!response.hasItem()) {
                return false;
            }

            AttributeValue allowAttr = response.item().get("allow");
            return allowAttr != null && allowAttr.bool();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}

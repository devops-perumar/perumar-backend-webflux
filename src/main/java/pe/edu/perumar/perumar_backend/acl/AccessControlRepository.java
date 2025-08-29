// src/main/java/pe/edu/perumar/perumar_backend/acl/AccessControlRepository.java
package pe.edu.perumar.perumar_backend.acl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

// AccessControlRepository.java
@Repository
@RequiredArgsConstructor
public class AccessControlRepository {
  private final DynamoDbEnhancedAsyncClient enhanced;
  private static final String TABLE = "perumar_access_control";

  private DynamoDbAsyncTable<AccessControlItem> table() {
    return enhanced.table(TABLE, TableSchema.fromBean(AccessControlItem.class));
  }

  public Flux<AccessControlItem> findByRole(String role) {
    if (role == null || role.isBlank()) return Flux.empty(); // evita IllegalArgumentException
    Key key = Key.builder().partitionValue(role).build();
    QueryEnhancedRequest req = QueryEnhancedRequest.builder()
        .queryConditional(QueryConditional.keyEqualTo(key))
        .build();
    return Flux.from(table().query(req)).flatMapIterable(Page::items);
  }
}


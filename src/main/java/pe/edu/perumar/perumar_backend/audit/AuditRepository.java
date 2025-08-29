// src/main/java/pe/edu/perumar/perumar_backend/audit/AuditRepository.java
package pe.edu.perumar.perumar_backend.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;

@Repository
@RequiredArgsConstructor
public class AuditRepository {
    private final DynamoDbEnhancedAsyncClient enhanced;

    @Value("${perumar.audit.ddb.enabled:true}")
    private boolean enabled;

    @Value("${perumar.audit.ddb.table:perumar_audit_menu_click}")
    private String tableName;

    private DynamoDbAsyncTable<AuditMenuClickItem> table() {
        return enhanced.table(tableName, TableSchema.fromBean(AuditMenuClickItem.class));
    }

    public Mono<Void> save(AuditMenuClickItem item) {
        if (!enabled) return Mono.empty();
        return Mono.fromFuture(table().putItem(item)).then();
    }
}

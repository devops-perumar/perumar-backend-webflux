package pe.edu.perumar.perumar_backend.ui.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pe.edu.perumar.perumar_backend.ui.model.UiMenuConfigEntity;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;

import java.util.Optional;

@Repository
public class UiConfigRepositoryImpl implements UiConfigRepository {

    private final DynamoDbEnhancedAsyncClient enhancedClient;
    private final DynamoDbAsyncTable<UiMenuConfigEntity> table;

    public UiConfigRepositoryImpl(
            DynamoDbEnhancedAsyncClient enhancedClient,
            @Value("${perumar.ui.config.table:perumar_ui_config}") String tableName
    ) {
        this.enhancedClient = enhancedClient;
        this.table = this.enhancedClient.table(tableName, TableSchema.fromBean(UiMenuConfigEntity.class));
    }

    @Override
    public Mono<UiMenuConfigEntity> findByRole(String role) {
        Key key = Key.builder()
            .partitionValue("ROLE#" + role)
            .sortValue("MENU#v1")  // si tu tabla tiene SK obligatoria
            .build();
        ;
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder().key(key).build();

        //log.info("➡️ Extracted role from JWT: {}", role);

        return Mono.fromFuture(table.getItem(request)
                .thenApply(Optional::ofNullable))
                .flatMap(opt -> opt.map(Mono::just).orElseGet(Mono::empty));
    }
}

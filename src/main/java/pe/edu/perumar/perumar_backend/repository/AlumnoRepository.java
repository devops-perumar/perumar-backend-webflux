package pe.edu.perumar.perumar_backend.repository;

import org.springframework.stereotype.Repository;
import pe.edu.perumar.perumar_backend.model.Alumno;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import reactor.core.publisher.Flux;

@Repository
public class AlumnoRepository {

    private final DynamoDbAsyncTable<Alumno> alumnoTable;

    public AlumnoRepository(DynamoDbAsyncClient dynamoDbAsyncClient) {
        DynamoDbEnhancedAsyncClient enhancedAsyncClient = DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient)
                .build();
        this.alumnoTable = enhancedAsyncClient.table("Alumno", TableSchema.fromBean(Alumno.class));
    }

    public Mono<Alumno> save(Alumno alumno) {
        return Mono.fromCompletionStage(alumnoTable.putItem(alumno).thenApply(res -> alumno));
    }

    public Mono<Alumno> findById(String alumnoId) {
        return Mono.fromCompletionStage(
            alumnoTable.getItem(r -> r.key(k -> k.partitionValue(alumnoId)))
        );
    }
    public Flux<Alumno> findAll() {
        return Flux.from(alumnoTable.scan().items());
    }

    public Mono<Void> deleteById(String alumnoId) {
        return Mono.fromCompletionStage(
            alumnoTable.deleteItem(r -> r.key(k -> k.partitionValue(alumnoId)))
        ).then();
    }
}

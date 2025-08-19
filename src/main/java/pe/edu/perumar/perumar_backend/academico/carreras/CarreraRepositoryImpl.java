package pe.edu.perumar.perumar_backend.academico.carreras;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import pe.edu.perumar.perumar_backend.academico.carreras.CarreraRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Repository
@Profile("!test")
public class CarreraRepositoryImpl implements CarreraRepository {

    private final DynamoDbAsyncClient dynamo;
    private final String tableName = "perumar_carreras";

    public CarreraRepositoryImpl(DynamoDbAsyncClient dynamo) {
        this.dynamo = dynamo;
    }

    @Override
    public Mono<Carrera> findByCodigo(String codigo) {
        Map<String, AttributeValue> key = Map.of("codigo", AttributeValue.builder().s(codigo).build());

        return Mono.fromFuture(() -> dynamo.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build()))
            .flatMap(resp -> resp.hasItem()
                    ? Mono.just(fromItem(resp.item()))
                    : Mono.empty());
    }


    @Override
    public Flux<Carrera> findAll(String estado) {

        ScanRequest.Builder reqBuilder = ScanRequest.builder().tableName(tableName);
        if (estado != null) {
            reqBuilder.filterExpression("estado = :estado")
                    .expressionAttributeValues(Map.of(":estado", AttributeValue.builder().s(estado).build()));
        }
        ScanRequest request = reqBuilder.build();

        return Mono.fromFuture(() -> dynamo.scan(request))
            .flatMapMany(resp -> Flux.fromIterable(resp.items()).map(this::fromItem));

    }

    @Override
    public Mono<Carrera> save(Carrera c) {
        return Mono.fromFuture(() -> dynamo.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(toItem(c))
                .build()))
            .thenReturn(c);
    }

    @Override
    public Mono<Carrera> update(Carrera c) {
        return save(c);
    }

    @Override
    public Mono<Void> updateEstado(String codigo, String estado) {
        Map<String, AttributeValue> key = Map.of("codigo", AttributeValue.builder().s(codigo).build());
        Map<String, AttributeValueUpdate> updates = Map.of(
                "estado", AttributeValueUpdate.builder()
                        .value(AttributeValue.builder().s(estado).build())
                        .action(AttributeAction.PUT)
                        .build()
        );
        return Mono.fromFuture(() -> dynamo.updateItem(UpdateItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .attributeUpdates(updates)
                .build()))
            .then();
    }

    private Carrera fromItem(Map<String, AttributeValue> item) {
        Carrera c = new Carrera();
        c.setCodigo(item.get("codigo").s());
        c.setNombre(item.get("nombre").s());
        c.setDescripcion(item.getOrDefault("descripcion", AttributeValue.builder().s("").build()).s());
        c.setModalidad(item.get("modalidad") != null ? 
            pe.edu.perumar.perumar_backend.academico.carreras.ModalidadCarrera.valueOf(item.get("modalidad").s()) : null);
        c.setMaterias(item.get("materias") != null ? 
            item.get("materias").ss() : java.util.Collections.emptyList());
        c.setEstado(item.get("estado").s());
        c.setCreatedAt(item.get("createdAt") != null ? java.time.Instant.parse(item.get("createdAt").s()) : null);
        c.setUpdatedAt(item.get("updatedAt") != null ? java.time.Instant.parse(item.get("updatedAt").s()) : null);
        return c;
    }

    private Map<String, AttributeValue> toItem(Carrera c) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("codigo", AttributeValue.builder().s(c.getCodigo()).build());
        item.put("nombre", AttributeValue.builder().s(c.getNombre()).build());
        if (c.getDescripcion() != null) item.put("descripcion", AttributeValue.builder().s(c.getDescripcion()).build());
        item.put("modalidad", AttributeValue.builder().s(c.getModalidad().name()).build());
        if (c.getMaterias() != null) item.put("materias", AttributeValue.builder().ss(c.getMaterias()).build());
        item.put("estado", AttributeValue.builder().s(c.getEstado()).build());
        if (c.getCreatedAt() != null) item.put("createdAt", AttributeValue.builder().s(c.getCreatedAt().toString()).build());
        if (c.getUpdatedAt() != null) item.put("updatedAt", AttributeValue.builder().s(c.getUpdatedAt().toString()).build());
        return item;
    }
}

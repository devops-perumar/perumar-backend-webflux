package pe.edu.perumar.perumar_backend.academico.carreras.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import pe.edu.perumar.perumar_backend.academico.carreras.model.Carrera;

import java.time.Instant;
import java.util.Objects;

@Repository
@Profile("!test")
public class CarreraRepositoryImpl implements CarreraRepository {
  private final DynamoDbAsyncTable<Carrera> table;

  public CarreraRepositoryImpl(DynamoDbEnhancedAsyncClient enhancedClient) {
    this.table = enhancedClient.table("perumar_carreras", TableSchema.fromBean(Carrera.class));
  }

  @Override
  public Mono<Carrera> save(Carrera c) {
    if (c.getCreatedAt() == null) c.setCreatedAt(Instant.now());
    c.setUpdatedAt(Instant.now());
    return Mono.fromFuture(table.putItem(c)).thenReturn(c);
  }

  @Override
  public Mono<Carrera> findByCodigo(String codigo) {
    return Mono.fromFuture(
            table.getItem(r -> r.key(Key.builder().partitionValue(codigo).build()))
        )
        .flatMap(Mono::justOrEmpty);
  }

  @Override
  public Flux<Carrera> findByEstado(String estado) {
    if (estado == null || estado.isBlank()) {
      return Flux.from(table.scan().items());
    }
    // Igual que materias: filtrar en memoria mientras no haya índice GSI
    return Flux.from(table.scan().items())
        .filter(c -> Objects.equals(c.getEstado(), estado));
  }

  @Override
  public Mono<Boolean> existsByCodigo(String codigo) {
    return findByCodigo(codigo).hasElement();
  }

  @Override
  public Mono<Carrera> update(Carrera c) {
    return Mono.fromFuture(table.updateItem(c));
  }

  @Override
  public Mono<Void> updateEstado(String codigo, String nuevoEstado) {
    return findByCodigo(codigo)
        .flatMap(c -> {
          c.setEstado(nuevoEstado);
          c.setUpdatedAt(Instant.now());
          return update(c).then();
        });
  }

  @Override
  public Mono<Void> deleteByCodigo(String codigo) {
    return Mono.fromFuture(() -> 
        table.deleteItem(r -> r.key(Key.builder().partitionValue(codigo).build()))
    ).then();
  }
}

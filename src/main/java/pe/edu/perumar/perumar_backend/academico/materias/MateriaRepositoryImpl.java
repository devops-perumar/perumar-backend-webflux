package pe.edu.perumar.perumar_backend.academico.materias;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Objects;

@Repository
@Profile("!test")
public class MateriaRepositoryImpl implements MateriaRepository {
  private final DynamoDbAsyncTable<Materia> table;

  public MateriaRepositoryImpl(DynamoDbEnhancedAsyncClient enhancedClient) {
    this.table = enhancedClient.table("perumar_materias", TableSchema.fromBean(Materia.class));
  }

  @Override
  public Mono<Materia> save(Materia m) {
    // set defaults si es necesario
    if (m.getCreatedAt() == null) m.setCreatedAt(Instant.now());
    m.setUpdatedAt(Instant.now());
    return Mono.fromFuture(table.putItem(m)).thenReturn(m);
  }

  @Override
  public Mono<Materia> findByCodigo(String codigo) {
    return Mono.fromFuture(table.getItem(r -> r.key(Key.builder().partitionValue(codigo).build())));
  }

  @Override
  public Flux<Materia> findByEstado(String estado) {
    if (estado == null || estado.isBlank()) {
      return Flux.from(table.scan().items());
    }
    // Si tienes GSI estado-index, cámbialo a query por índice. Para MVP, filtro en memoria:
    return Flux.from(table.scan().items())
        .filter(m -> Objects.equals(m.getEstado(), estado));
  }

  @Override
  public Mono<Boolean> existsByCodigo(String codigo) {
    return findByCodigo(codigo).map(Objects::nonNull);
  }

  @Override
  public Mono<Materia> update(Materia m) {
    return Mono.fromFuture(table.updateItem(m));
  }

  @Override
  public Mono<Void> updateEstado(String codigo, String nuevoEstado) {
    return findByCodigo(codigo)
        .flatMap(m -> {
          if (m == null) return Mono.empty();
          m.setEstado(nuevoEstado);
          return update(m).then();
        });
  }
}

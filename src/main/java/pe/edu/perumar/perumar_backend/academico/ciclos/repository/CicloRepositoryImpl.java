package pe.edu.perumar.perumar_backend.academico.ciclos.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import pe.edu.perumar.perumar_backend.academico.ciclos.model.Ciclo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import java.time.Instant;
import java.time.LocalDate;

@Repository
@Profile("!test")
public class CicloRepositoryImpl implements CicloRepository {
  private final DynamoDbAsyncTable<Ciclo> table;

  public CicloRepositoryImpl(DynamoDbEnhancedAsyncClient enhancedClient) {
    this.table = enhancedClient.table("perumar_ciclos", TableSchema.fromBean(Ciclo.class));
  }

  @Override
  public Mono<Ciclo> save(Ciclo c) {
    if (c.getCreatedAt() == null) c.setCreatedAt(Instant.now());
    c.setUpdatedAt(Instant.now());
    return Mono.fromFuture(table.putItem(c)).thenReturn(c);
  }

  @Override
  public Mono<Ciclo> update(Ciclo c) {
    c.setUpdatedAt(Instant.now());
    return Mono.fromFuture(table.updateItem(c));
  }

  @Override
  public Mono<Ciclo> findById(String id) {
    return Mono.fromFuture(table.getItem(r -> r.key(Key.builder().partitionValue(id).build())));
  }

  @Override
  public Flux<Ciclo> findByEstado(String estado) {
    if (estado == null || estado.isBlank()) {
      return Flux.from(table.scan())
          .flatMap(page -> Flux.fromIterable(page.items()));
    }
    return Flux.from(
            table.index("estado-index")
                .query(r -> r.queryConditional(
                    QueryConditional.keyEqualTo(Key.builder().partitionValue(estado).build()))))
        .flatMap(page -> Flux.fromIterable(page.items()));
  }

  @Override
  public Flux<Ciclo> findByCodigoCarrera(String codigoCarrera) {
    if (codigoCarrera == null || codigoCarrera.isBlank()) {
      return Flux.empty();
    }
    return Flux.from(
            table.index("carrera-index")
                .query(r -> r.queryConditional(
                    QueryConditional.keyEqualTo(Key.builder().partitionValue(codigoCarrera).build()))))
        .flatMap(page -> Flux.fromIterable(page.items()));
  }

  @Override
  public Flux<Ciclo> findOverlaps(String codigoCarrera, LocalDate ini, LocalDate fin) {
    return findByCodigoCarrera(codigoCarrera)
        .filter(c -> c.getFechaInicio() != null && c.getFechaFin() != null)
        .filter(c -> !ini.isAfter(c.getFechaFin()) && !fin.isBefore(c.getFechaInicio()));
  }

  @Override
  public Mono<Integer> nextCorrelativo(String codigoCarrera, int year) {
    return findByCodigoCarrera(codigoCarrera)
        .filter(c -> c.getFechaInicio() != null && c.getFechaInicio().getYear() == year)
        .count()
        .map(cnt -> cnt.intValue() + 1);
  }
}

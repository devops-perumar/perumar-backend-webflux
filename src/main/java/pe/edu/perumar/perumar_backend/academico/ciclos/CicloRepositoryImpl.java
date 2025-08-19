package pe.edu.perumar.perumar_backend.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pe.edu.perumar.perumar_backend.model.Ciclo;
import pe.edu.perumar.perumar_backend.repository.CicloRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Repository
@Profile("!test")
public class CicloRepositoryImpl implements CicloRepository {

  @Override
  public Mono<Ciclo> save(Ciclo c) {
    // TODO: persistir en DynamoDB (tabla perumar_ciclos)
    return Mono.just(c);
  }

  @Override
  public Mono<Ciclo> update(Ciclo c) {
    // TODO: update en DynamoDB + set updatedAt
    return Mono.just(c);
  }

  @Override
  public Mono<Ciclo> findById(String id) {
    // TODO: getItem por PK=id
    return Mono.empty();
  }

  @Override
  public Flux<Ciclo> findByEstado(String estado) {
    // TODO: usar GSI estado-index (estado → fechaInicio) si lo creas
    return Flux.empty();
  }

  @Override
  public Flux<Ciclo> findByCodigoCarrera(String codigoCarrera) {
    // TODO: usar GSI carrera-index (codigoCarrera → fechaInicio)
    return Flux.empty();
  }

  @Override
  public Flux<Ciclo> findOverlaps(String codigoCarrera, LocalDate ini, LocalDate fin) {
    // TODO: query por codigoCarrera y filtrar por rango que se superpone
    return Flux.empty();
  }

  @Override
  public Mono<Integer> nextCorrelativo(String codigoCarrera, int year) {
    // TODO: contar ciclos del año para esa carrera y devolver (count+1)
    return Mono.just(1);
  }
}

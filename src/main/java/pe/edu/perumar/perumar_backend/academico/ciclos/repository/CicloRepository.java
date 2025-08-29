package pe.edu.perumar.perumar_backend.academico.ciclos.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

import pe.edu.perumar.perumar_backend.academico.ciclos.model.Ciclo;

public interface CicloRepository {
  Mono<Ciclo> save(Ciclo c);
  Mono<Ciclo> findById(String id);
  Flux<Ciclo> findByEstado(String estado);
  Flux<Ciclo> findByCodigoCarrera(String codigoCarrera);
  Flux<Ciclo> findOverlaps(String codigoCarrera, LocalDate ini, LocalDate fin);
  Mono<Integer> nextCorrelativo(String codigoCarrera, int year);
  Mono<Ciclo> update(Ciclo c);
}

package pe.edu.perumar.perumar_backend.academico.materias.repository;

import pe.edu.perumar.perumar_backend.academico.materias.model.Materia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MateriaRepository {
  Mono<Materia> save(Materia m);
  Mono<Materia> findByCodigo(String codigo);
  Flux<Materia> findByEstado(String estado);
  Mono<Boolean> existsByCodigo(String codigo);
  Mono<Materia> update(Materia m);
  Mono<Void> updateEstado(String codigo, String nuevoEstado);
  Mono<Void> deleteByCodigo(String codigo);
}

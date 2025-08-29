package pe.edu.perumar.perumar_backend.academico.carreras.repository;

import pe.edu.perumar.perumar_backend.academico.carreras.model.Carrera;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CarreraRepository {
    Mono<Carrera> save(Carrera c);
    Mono<Carrera> findByCodigo(String codigo);
    Flux<Carrera> findByEstado(String estado);
    Mono<Boolean> existsByCodigo(String codigo);
    Mono<Carrera> update(Carrera c);
    Mono<Void> updateEstado(String codigo, String nuevoEstado);
    Mono<Void> deleteByCodigo(String codigo);
}

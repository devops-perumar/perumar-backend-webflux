package pe.edu.perumar.perumar_backend.repository;

import pe.edu.perumar.perumar_backend.model.Carrera;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CarreraRepository {
    Mono<Carrera> findByCodigo(String codigo);
    Flux<Carrera> findAll(String estado);
    Mono<Carrera> save(Carrera carrera);
    Mono<Carrera> update(Carrera carrera);
    Mono<Void> updateEstado(String codigo, String estado);
}

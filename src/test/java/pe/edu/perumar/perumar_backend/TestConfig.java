package pe.edu.perumar.perumar_backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import pe.edu.perumar.perumar_backend.academico.ciclos.model.Ciclo;
import pe.edu.perumar.perumar_backend.academico.ciclos.repository.CicloRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@TestConfiguration
public class TestConfig {

  @Bean
  CicloRepository cicloRepository() {
    return new CicloRepository() {
      final Map<String,Ciclo> store = new ConcurrentHashMap<>();

      @Override public Mono<Ciclo> save(Ciclo c) {
        if (c.getId() == null) c.setId(UUID.randomUUID().toString());
        c.setCreatedAt(Instant.now()); c.setUpdatedAt(Instant.now());
        store.put(c.getId(), c); return Mono.just(c);
      }
      @Override public Mono<Ciclo> update(Ciclo c) {
        c.setUpdatedAt(Instant.now()); store.put(c.getId(), c); return Mono.just(c);
      }
      @Override public Mono<Ciclo> findById(String id) {
        return Mono.justOrEmpty(store.get(id));
      }
      @Override public Flux<Ciclo> findByEstado(String estado) {
        return Flux.fromStream(store.values().stream().filter(x -> estado.equals(x.getEstado())));
      }
      @Override public Flux<Ciclo> findByCodigoCarrera(String codigoCarrera) {
        return Flux.fromStream(store.values().stream().filter(x -> codigoCarrera.equals(x.getCodigoCarrera())));
      }
      @Override public Flux<Ciclo> findOverlaps(String codigoCarrera, LocalDate ini, LocalDate fin) {
        return findByCodigoCarrera(codigoCarrera)
            .filter(c -> !ini.isAfter(c.getFechaFin()) && !fin.isBefore(c.getFechaInicio()));
      }
      @Override public Mono<Integer> nextCorrelativo(String codigoCarrera, int year) {
        long count = store.values().stream()
            .filter(c -> codigoCarrera.equals(c.getCodigoCarrera()))
            .filter(c -> c.getFechaInicio() != null && c.getFechaInicio().getYear() == year)
            .count();
        return Mono.just((int)count + 1);
      }
    };
  }
}

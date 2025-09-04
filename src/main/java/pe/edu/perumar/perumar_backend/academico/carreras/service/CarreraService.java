package pe.edu.perumar.perumar_backend.academico.carreras.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraResponse;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraUpdateRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.mapper.CarreraMapper;
import pe.edu.perumar.perumar_backend.academico.carreras.model.Carrera;
import pe.edu.perumar.perumar_backend.academico.carreras.repository.CarreraRepository;
import pe.edu.perumar.perumar_backend.academico.materias.repository.MateriaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CarreraService {

  private final CarreraRepository repo;
  private final MateriaRepository materiaRepo; // 👈 agregado para validar materias

  public CarreraService(CarreraRepository repo, MateriaRepository materiaRepo) {
    this.repo = repo;
    this.materiaRepo = materiaRepo;
  }

  public Mono<Carrera> crear(CarreraRequest req) {
    String codigo = req.getCodigo();
    if (!StringUtils.hasText(codigo)) {
      return Mono.error(new IllegalArgumentException("codigo requerido"));
    }

    if (req.getMaterias() == null || req.getMaterias().isEmpty()) {
      return Mono.error(new IllegalArgumentException("Debe registrar al menos una materia"));
    }

    // validar que todas las materias existan
    return Flux.fromIterable(req.getMaterias())
        .flatMap(codMat -> materiaRepo.findByCodigo(codMat)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Materia inexistente: " + codMat))))
        .then(
            repo.findByCodigo(codigo)
                .flatMap(existing -> Mono.<Carrera>error(
                    new DuplicateKeyException("Carrera ya existe: " + codigo)))
                .switchIfEmpty(Mono.defer(() -> {
                  Carrera c = new Carrera();
                  c.setCodigo(req.getCodigo());
                  c.setNombre(req.getNombre());
                  c.setDescripcion(req.getDescripcion());
                  c.setModalidad(req.getModalidad());
                  c.setMaterias(req.getMaterias());
                  c.setEstado("ACTIVO");
                  Instant now = Instant.now();
                  c.setCreatedAt(now);
                  c.setUpdatedAt(now);
                  return repo.save(c);
                }))
        );
  }

  public Mono<Carrera> obtener(String codigo) {
    return repo.findByCodigo(codigo)
               .switchIfEmpty(Mono.empty());
  }

  public Flux<CarreraResponse> listar(String estado) {
    String estadoFinal = StringUtils.hasText(estado) ? estado : "ACTIVO";
    return repo.findByEstado(estadoFinal)
               .map(CarreraMapper::toResponse);
  }

  public Mono<Carrera> actualizar(String codigo, CarreraUpdateRequest req) {
    return repo.findByCodigo(codigo)
        .flatMap(actual -> {
          // 1) Validar materias si vienen en el request
          Mono<Void> validarMaterias = Mono.empty();
          if (req.getMaterias() != null) {
            validarMaterias = Flux.fromIterable(req.getMaterias())
                .flatMap(codMat -> materiaRepo.findByCodigo(codMat)
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Materia inexistente: " + codMat))))
                .then();
          }

          // 2) Aplicar cambios y persistir
          return validarMaterias.then(Mono.defer(() -> {
            if (StringUtils.hasText(req.getNombre())) {
              actual.setNombre(req.getNombre());
            }
            if (req.getDescripcion() != null) {
              actual.setDescripcion(req.getDescripcion());
            }
            if (req.getModalidad() != null) {
              actual.setModalidad(req.getModalidad());
            }
            if (req.getMaterias() != null) {
              actual.setMaterias(req.getMaterias());
            }
            actual.setUpdatedAt(Instant.now());
            return repo.update(actual);
          }));
        })
        .switchIfEmpty(Mono.empty());
    }

  public Mono<Void> cambiarEstado(String codigo, CarreraEstadoRequest req) {
    return repo.updateEstado(codigo, req.getEstado())
               .switchIfEmpty(Mono.empty());
  }

  public static class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException(String msg) { super(msg); }
  }

  public Mono<Void> eliminar(String codigo) {
    return repo.deleteByCodigo(codigo)
               .switchIfEmpty(Mono.empty());
  }

}

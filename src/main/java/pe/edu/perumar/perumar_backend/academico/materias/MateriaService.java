package pe.edu.perumar.perumar_backend.academico.materias;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaResponse;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MateriaService {

  private final MateriaRepository repo;

  public MateriaService(MateriaRepository repo) {
    this.repo = repo;
  }

  public Mono<Materia> crear(MateriaRequest req) {
    // Validación mínima adicional (además de @Valid en controller)
    String codigo = req.getCodigo();
    if (!StringUtils.hasText(codigo)) {
      return Mono.error(new IllegalArgumentException("codigo requerido"));
    }

    return repo.findByCodigo(codigo)
        // Si existe, error de duplicado
        .flatMap(existing -> Mono.<Materia>error(new DuplicateKeyException("Materia ya existe: " + codigo)))
        // Si NO existe, construimos y guardamos
        .switchIfEmpty(Mono.defer(() -> {
          Materia m = new Materia();
          m.setCodigo(req.getCodigo());
          m.setNombre(req.getNombre());
          m.setDescripcion(req.getDescripcion());
          m.setEstado("ACTIVO"); // estado inicial fijo
          Instant now = Instant.now();
          m.setCreatedAt(now);
          m.setUpdatedAt(now);
          return repo.save(m);
        }));
  }

  public Mono<Materia> obtener(String codigo) {
    return repo.findByCodigo(codigo);
  }

public Flux<MateriaResponse> listar(String estado) {
    String estadoFinal = StringUtils.hasText(estado) ? estado : "ACTIVO";
    return repo.findByEstado(estadoFinal)
               .map(MateriaMapper::toResponse);
}

  public Mono<Materia> actualizar(String codigo, MateriaUpdateRequest req) {
    return repo.findByCodigo(codigo)
        .flatMap(actual -> {
          // aplicar cambios permitidos
          if (StringUtils.hasText(req.getNombre())) {
            actual.setNombre(req.getNombre());
          }
          if (req.getDescripcion() != null) {
            actual.setDescripcion(req.getDescripcion());
          }
          actual.setUpdatedAt(Instant.now());
          return repo.update(actual);
        });
  }

  public Mono<Void> cambiarEstado(String codigo, MateriaEstadoRequest req) {
    // PATCH solo cambia estado
    return repo.updateEstado(codigo, req.getEstado());
  }

  // Excepción pequeña para 409
  public static class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException(String msg) { super(msg); }
  }

  public Mono<Void> eliminar(String codigo) {
      return repo.deleteByCodigo(codigo);
  }

}

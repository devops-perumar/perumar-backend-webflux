package pe.edu.perumar.perumar_backend.academico.materias;

import java.time.Instant;

import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaResponse;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaUpdateRequest;

public class MateriaMapper {

  public static Materia toEntityOnCreate(MateriaRequest req) {
    Materia m = new Materia();
    m.setCodigo(req.getCodigo());
    m.setNombre(req.getNombre());
    m.setDescripcion(req.getDescripcion());
    m.setEstado("ACTIVO");
    Instant now = Instant.now();
    m.setCreatedAt(now);
    m.setUpdatedAt(now);
    return m;
  }

  public static void applyUpdate(Materia m, MateriaUpdateRequest req) {
    m.setNombre(req.getNombre());
    m.setDescripcion(req.getDescripcion());
    m.setUpdatedAt(Instant.now());
  }

  public static void applyEstado(Materia m, MateriaEstadoRequest req) {
    m.setEstado(req.getEstado());
    m.setUpdatedAt(Instant.now());
  }

  public static MateriaResponse toResponse(Materia m) {
    MateriaResponse r = new MateriaResponse();
    r.setCodigo(m.getCodigo());
    r.setNombre(m.getNombre());
    r.setDescripcion(m.getDescripcion());
    r.setEstado(m.getEstado());
    r.setCreatedAt(m.getCreatedAt());
    r.setUpdatedAt(m.getUpdatedAt());
    return r;
  }
}

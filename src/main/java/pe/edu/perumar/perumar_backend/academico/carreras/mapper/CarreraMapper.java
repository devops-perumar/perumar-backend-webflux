package pe.edu.perumar.perumar_backend.academico.carreras.mapper;

import java.time.Instant;

import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraResponse;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraUpdateRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.model.Carrera;

public class CarreraMapper {

  public static Carrera toEntityOnCreate(CarreraRequest req) {
    Carrera c = new Carrera();
    c.setCodigo(req.getCodigo());
    c.setNombre(req.getNombre());
    c.setDescripcion(req.getDescripcion());
    c.setModalidad(req.getModalidad());       // enum ya llega en el DTO
    c.setMaterias(req.getMaterias());         // 🔄 cambiado de getCarreras() → getMaterias()
    c.setEstado("ACTIVO");
    Instant now = Instant.now();
    c.setCreatedAt(now);
    c.setUpdatedAt(now);
    return c;
  }

  public static void applyUpdate(Carrera actual, CarreraUpdateRequest req) {
    actual.setNombre(req.getNombre());
    actual.setDescripcion(req.getDescripcion());
    actual.setModalidad(req.getModalidad());  
    actual.setMaterias(req.getMaterias());    // ✅ correcto, aquí sí existía
    actual.setUpdatedAt(Instant.now());
  }

  public static CarreraResponse toResponse(Carrera c) {
    CarreraResponse r = new CarreraResponse();
    r.setCodigo(c.getCodigo());
    r.setNombre(c.getNombre());
    r.setDescripcion(c.getDescripcion());
    r.setModalidad(c.getModalidad() != null ? c.getModalidad().name() : null);
    r.setMaterias(c.getMaterias());
    r.setEstado(c.getEstado());
    r.setCreatedAt(c.getCreatedAt());
    r.setUpdatedAt(c.getUpdatedAt());
    return r;
  }
}

package pe.edu.perumar.perumar_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pe.edu.perumar.perumar_backend.dto.CicloRequest;
import pe.edu.perumar.perumar_backend.dto.CicloResponse;
import pe.edu.perumar.perumar_backend.model.Ciclo;
import pe.edu.perumar.perumar_backend.repository.CicloRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class CicloService {

  private final CicloRepository repo;

  public CicloService(CicloRepository repo) { this.repo = repo; }

  public Mono<CicloResponse> crear(CicloRequest req){
    if (req.fechaInicio().isAfter(req.fechaFin()) || req.fechaInicio().isEqual(req.fechaFin())) {
      return Mono.error(new IllegalArgumentException("fechaInicio debe ser menor que fechaFin"));
    }
    return repo.findOverlaps(req.codigoCarrera(), req.fechaInicio(), req.fechaFin())
      .hasElements()
      .flatMap(exists -> {
        if (exists) return Mono.error(new IllegalStateException("El rango de fechas se superpone con otro ciclo"));
        int year = req.fechaInicio().getYear();
        return repo.nextCorrelativo(req.codigoCarrera(), year)
          .flatMap(corr -> {
            String nombre = StringUtils.hasText(req.nombreCiclo())
              ? req.nombreCiclo()
              : String.format("%d-%02d-%s", year, corr, req.codigoCarrera());
            Ciclo c = Ciclo.nuevo(req.codigoCarrera());
            c.setNombreCiclo(nombre);
            c.setFechaInicio(req.fechaInicio());
            c.setFechaFin(req.fechaFin());
            c.setMaterias(req.materias());
            c.setPromocion(req.promocion());
            c.setUbicacion(req.ubicacion());
            return repo.save(c);
          });
      })
      .map(CicloService::toResponse);
  }

  public Mono<CicloResponse> obtener(String id){
    return repo.findById(id).switchIfEmpty(Mono.error(new IllegalArgumentException("Ciclo no encontrado")))
      .map(CicloService::toResponse);
  }

  public Flux<CicloResponse> listarPorCarrera(String codigoCarrera){
    return repo.findByCodigoCarrera(codigoCarrera).map(CicloService::toResponse);
  }

  public Flux<CicloResponse> listarPorEstado(String estado){
    return repo.findByEstado(estado).map(CicloService::toResponse);
  }

  public Mono<CicloResponse> actualizar(String id, CicloRequest req, boolean tieneMatriculas, boolean esDirector){
    if (req.fechaInicio().isAfter(req.fechaFin()) || req.fechaInicio().isEqual(req.fechaFin())) {
      return Mono.error(new IllegalArgumentException("fechaInicio debe ser menor que fechaFin"));
    }
    return repo.findById(id).switchIfEmpty(Mono.error(new IllegalArgumentException("Ciclo no encontrado")))
      .flatMap(actual -> {
        if (tieneMatriculas && !esDirector) {
          return Mono.error(new IllegalStateException("Solo DIRECTOR puede modificar ciclo con matrículas"));
        }
        // Regla: con matrículas, limitar campos (promoción, fechas)
        actual.setUpdatedAt(Instant.now());
        actual.setFechaInicio(req.fechaInicio());
        actual.setFechaFin(req.fechaFin());
        if (StringUtils.hasText(req.promocion())) actual.setPromocion(req.promocion());
        if (!tieneMatriculas) {
          // Campos libres si no hay matrículas
          actual.setMaterias(req.materias());
          if (req.nombreCiclo()!=null) actual.setNombreCiclo(req.nombreCiclo());
          if (req.ubicacion()!=null) actual.setUbicacion(req.ubicacion());
        }
        return repo.update(actual);
      })
      .map(CicloService::toResponse);
  }

  public Mono<Void> cambiarEstado(String id, String nuevoEstado){
    return repo.findById(id).switchIfEmpty(Mono.error(new IllegalArgumentException("Ciclo no encontrado")))
      .flatMap(c -> { c.setEstado(nuevoEstado); c.setUpdatedAt(Instant.now()); return repo.update(c); })
      .then();
  }

  private static CicloResponse toResponse(Ciclo c){
    return new CicloResponse(
      c.getId(), c.getCodigoCarrera(), c.getNombreCiclo(),
      c.getFechaInicio(), c.getFechaFin(),
      c.getMaterias(), c.getPromocion(), c.getUbicacion(),
      c.getEstado(), c.getCreatedAt(), c.getUpdatedAt()
    );
  }
}

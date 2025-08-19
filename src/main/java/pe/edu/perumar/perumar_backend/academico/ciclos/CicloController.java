package pe.edu.perumar.perumar_backend.academico.ciclos;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pe.edu.perumar.perumar_backend.academico.ciclos.dto.CicloRequest;
import pe.edu.perumar.perumar_backend.academico.ciclos.dto.CicloResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ciclos")
public class CicloController {

  private final CicloService service;

  public CicloController(CicloService service){ this.service = service; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN','COORDINADOR')")
  public Mono<CicloResponse> crear(@Valid @RequestBody CicloRequest req){
    return service.crear(req);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN','COORDINADOR')")
  public Mono<CicloResponse> get(@PathVariable String id){
    return service.obtener(id);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN','COORDINADOR')")
  public Flux<CicloResponse> list(
      @RequestParam(required=false) String estado,
      @RequestParam(required=false) String codigoCarrera){
    if (codigoCarrera != null) return service.listarPorCarrera(codigoCarrera);
    if (estado != null) return service.listarPorEstado(estado);
    return service.listarPorEstado("ACTIVO"); // default
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN','COORDINADOR')")
  public Mono<CicloResponse> update(
      @PathVariable String id,
      @Valid @RequestBody CicloRequest req,
      @RequestHeader(name="X-Tiene-Matriculas", defaultValue="false") boolean tieneMatriculas,
      @RequestHeader(name="X-Es-Director", defaultValue="false") boolean esDirector){
    return service.actualizar(id, req, tieneMatriculas, esDirector);
  }

  @PatchMapping("/{id}/estado")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
  public Mono<Void> cambiarEstado(@PathVariable String id, @RequestParam String estado){
    return service.cambiarEstado(id, estado);
  }
}

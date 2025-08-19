package pe.edu.perumar.perumar_backend.academico.materias;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pe.edu.perumar.perumar_backend.academico.materias.MateriaService.DuplicateKeyException;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaResponse;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/materias")
@Validated
public class MateriaController {

  private final MateriaService service;

  public MateriaController(MateriaService service) {
    this.service = service;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
  public Mono<ResponseEntity<MateriaResponse>> crear(@Valid @RequestBody MateriaRequest req) {
    return service.crear(req)
        .map(MateriaMapper::toResponse)
        .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR','COORDINADOR')")
  public Flux<MateriaResponse> listar(@RequestParam(value = "estado", required = false) String estado) {
    return service.listar(estado);
  }

  @GetMapping("/{codigo}")
  @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR','COORDINADOR')")
  public Mono<MateriaResponse> obtener(@PathVariable String codigo) {
    return service.obtener(codigo).map(MateriaMapper::toResponse);
  }

  @PutMapping("/{codigo}")
  @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
  public Mono<ResponseEntity<MateriaResponse>> actualizar(
      @PathVariable String codigo,
      @Valid @RequestBody MateriaUpdateRequest req) {
    return service.actualizar(codigo, req)
        .map(MateriaMapper::toResponse)
        .map(ResponseEntity::ok);
  }

  @PatchMapping("/{codigo}/estado")
  @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
  public Mono<ResponseEntity<Void>> cambiarEstado(
      @PathVariable String codigo,
      @Valid @RequestBody MateriaEstadoRequest req) {
    return service.cambiarEstado(codigo, req)
        .thenReturn(ResponseEntity.noContent().build());
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateKeyException.class)
  public void onDuplicate() { /* 409 sin body */ }
}

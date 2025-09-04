package pe.edu.perumar.perumar_backend.academico.materias.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaResponse;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaUpdateRequest;
import pe.edu.perumar.perumar_backend.academico.materias.mapper.MateriaMapper;
import pe.edu.perumar.perumar_backend.academico.materias.service.MateriaService;
import pe.edu.perumar.perumar_backend.academico.materias.service.MateriaService.DuplicateKeyException;
import pe.edu.perumar.perumar_backend.acl.AccessGuard;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/materias")
@Validated
public class MateriaController {

  private final MateriaService service;
  private final AccessGuard accessGuard;

  public MateriaController(MateriaService service, AccessGuard accessGuard) {
    this.service = service;
    this.accessGuard = accessGuard;
  }

  @PostMapping
  public Mono<ResponseEntity<MateriaResponse>> crear(@Valid @RequestBody MateriaRequest req) {
    return accessGuard.requireMono(
        "/api/v1/materias", "create",
        () -> service.crear(req)
            .map(MateriaMapper::toResponse)
            .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body))
    );
  }

  @GetMapping
  public Flux<MateriaResponse> listar(@RequestParam(value = "estado", required = false) String estado) {
    return accessGuard.requireFlux(
        "/api/v1/materias", "read",
        () -> service.listar(estado)
    );
  }

  @GetMapping("/{codigo}")
  public Mono<MateriaResponse> obtener(@PathVariable String codigo) {
    return accessGuard.requireMono(
        "/api/v1/materias", "read",
        () -> service.obtener(codigo).map(MateriaMapper::toResponse)
    );
  }

  @PutMapping("/{codigo}")
  public Mono<ResponseEntity<MateriaResponse>> actualizar(
      @PathVariable String codigo,
      @Valid @RequestBody MateriaUpdateRequest req) {
    return accessGuard.requireMono(
        "/api/v1/materias", "edit",
        () -> service.actualizar(codigo, req)
            .map(MateriaMapper::toResponse)
            .map(ResponseEntity::ok)
    );
  }

  @PatchMapping("/{codigo}/estado")
  public Mono<ResponseEntity<Void>> cambiarEstado(
      @PathVariable String codigo,
      @Valid @RequestBody MateriaEstadoRequest req) {
    return accessGuard.requireMono(
        "/api/v1/materias", "edit_estado",
        () -> service.cambiarEstado(codigo, req).thenReturn(ResponseEntity.noContent().build())
    );
  }

  @DeleteMapping("/{codigo}")
  public Mono<ResponseEntity<Void>> eliminar(@PathVariable String codigo) {
    return accessGuard.requireMono(
        "/api/v1/materias", "delete",
        () -> service.eliminar(codigo).thenReturn(ResponseEntity.noContent().build())
    );
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateKeyException.class)
  public void onDuplicate() { /* 409 sin body */ }
}

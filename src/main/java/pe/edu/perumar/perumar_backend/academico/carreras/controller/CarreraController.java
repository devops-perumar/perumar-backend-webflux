package pe.edu.perumar.perumar_backend.academico.carreras.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraResponse;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraUpdateRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.mapper.CarreraMapper;
import pe.edu.perumar.perumar_backend.academico.carreras.service.CarreraService;
import pe.edu.perumar.perumar_backend.academico.carreras.service.CarreraService.DuplicateKeyException;
import pe.edu.perumar.perumar_backend.acl.AccessGuard;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/carreras")
@Validated
public class CarreraController {

  private final CarreraService service;
  private final AccessGuard accessGuard;

  public CarreraController(CarreraService service, AccessGuard accessGuard) {
    this.service = service;
    this.accessGuard = accessGuard;
  }

  @PostMapping
  public Mono<ResponseEntity<CarreraResponse>> crear(@Valid @RequestBody CarreraRequest req) {
    return accessGuard.requireMono(
        "/api/v1/carreras", "create",
        () -> service.crear(req)
            .map(CarreraMapper::toResponse)
            .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body))
    );
  }

  @GetMapping
  public Flux<CarreraResponse> listar(@RequestParam(value = "estado", required = false) String estado) {
    return accessGuard.requireFlux(
        "/api/v1/carreras", "read",
        () -> service.listar(estado)
    );
  }

  @GetMapping("/{codigo}")
  public Mono<CarreraResponse> obtener(@PathVariable String codigo) {
    return accessGuard.requireMono(
        "/api/v1/carreras", "read",
        () -> service.obtener(codigo).map(CarreraMapper::toResponse)
    );
  }

  @PutMapping("/{codigo}")
  public Mono<ResponseEntity<CarreraResponse>> actualizar(
      @PathVariable String codigo,
      @Valid @RequestBody CarreraUpdateRequest req) {
    return accessGuard.requireMono(
        "/api/v1/carreras", "edit",
        () -> service.actualizar(codigo, req)
            .map(CarreraMapper::toResponse)
            .map(ResponseEntity::ok)
    );
  }

  @PatchMapping("/{codigo}/estado")
  public Mono<ResponseEntity<Void>> cambiarEstado(
      @PathVariable String codigo,
      @Valid @RequestBody CarreraEstadoRequest req) {
    return accessGuard.requireMono(
        "/api/v1/carreras", "edit_estado",
        () -> service.cambiarEstado(codigo, req).thenReturn(ResponseEntity.noContent().build())
    );
  }

  @DeleteMapping("/{codigo}")
  public Mono<ResponseEntity<Void>> eliminar(@PathVariable String codigo) {
    return accessGuard.requireMono(
        "/api/v1/carreras", "delete",
        () -> service.eliminar(codigo).thenReturn(ResponseEntity.noContent().build())
    );
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateKeyException.class)
  public void onDuplicate() { /* 409 sin body */ }
}

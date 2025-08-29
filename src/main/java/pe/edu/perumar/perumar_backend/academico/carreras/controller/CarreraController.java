package pe.edu.perumar.perumar_backend.academico.carreras.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraResponse;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraUpdateRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.mapper.CarreraMapper;
import pe.edu.perumar.perumar_backend.academico.carreras.service.CarreraService;
import pe.edu.perumar.perumar_backend.academico.carreras.service.CarreraService.DuplicateKeyException;
import pe.edu.perumar.perumar_backend.acl.AccessControlService;
import pe.edu.perumar.perumar_backend.config.SecurityUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/carreras")
@Validated
public class CarreraController {

  private final CarreraService service;
  private final AccessControlService accessControlService;

  public CarreraController(CarreraService service, AccessControlService accessControlService) {
    this.service = service;
    this.accessControlService = accessControlService;
  }

  @PostMapping
  public Mono<ResponseEntity<CarreraResponse>> crear(@Valid @RequestBody CarreraRequest req) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/carreras", "create", "BACKEND");
            return req;
        })
        .flatMap(service::crear)
        .map(CarreraMapper::toResponse)
        .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @GetMapping
  public Flux<CarreraResponse> listar(@RequestParam(value = "estado", required = false) String estado) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/carreras", "read", "BACKEND");
            return estado;
        })
        .flatMapMany(service::listar);
  }

  @GetMapping("/{codigo}")
  public Mono<CarreraResponse> obtener(@PathVariable String codigo) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/carreras", "read", "BACKEND");
            return codigo;
        })
        .flatMap(service::obtener)
        .map(CarreraMapper::toResponse);
  }

  @PutMapping("/{codigo}")
  public Mono<ResponseEntity<CarreraResponse>> actualizar(
      @PathVariable String codigo,
      @Valid @RequestBody CarreraUpdateRequest req) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/carreras", "update", "BACKEND");
            return codigo;
        })
        .flatMap(id -> service.actualizar(id, req))
        .map(CarreraMapper::toResponse)
        .map(ResponseEntity::ok);
  }

  @PatchMapping("/{codigo}/estado")
  public Mono<ResponseEntity<Void>> cambiarEstado(
      @PathVariable String codigo,
      @Valid @RequestBody CarreraEstadoRequest req) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/carreras", "update_estado", "BACKEND");
            return codigo;
        })
        .flatMap(id -> service.cambiarEstado(id, req))
        .thenReturn(ResponseEntity.noContent().build());
  }


  @DeleteMapping("/{codigo}")
  public Mono<ResponseEntity<Void>> eliminar(@PathVariable String codigo) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/carreras", "delete", "BACKEND");
            return codigo;
        })
        .flatMap(service::eliminar)
        .thenReturn(ResponseEntity.noContent().build());
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateKeyException.class)
  public void onDuplicate() { /* 409 sin body */ }
}

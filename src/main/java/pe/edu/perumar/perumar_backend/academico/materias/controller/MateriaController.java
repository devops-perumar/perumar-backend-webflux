package pe.edu.perumar.perumar_backend.academico.materias.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaResponse;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaUpdateRequest;
import pe.edu.perumar.perumar_backend.academico.materias.mapper.MateriaMapper;
import pe.edu.perumar.perumar_backend.academico.materias.service.MateriaService;
import pe.edu.perumar.perumar_backend.academico.materias.service.MateriaService.DuplicateKeyException;
import pe.edu.perumar.perumar_backend.acl.AccessControlService;
import pe.edu.perumar.perumar_backend.config.SecurityUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/materias")
@Validated
public class MateriaController {

  private final MateriaService service;
  private final AccessControlService accessControlService;

  public MateriaController(MateriaService service, AccessControlService accessControlService) {
    this.service = service;
    this.accessControlService = accessControlService;
  }

  @PostMapping
  public Mono<ResponseEntity<MateriaResponse>> crear(@Valid @RequestBody MateriaRequest req) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/materias", "create", "BACKEND");
            return req;
        })
        .flatMap(service::crear)
        .map(MateriaMapper::toResponse)
        .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @GetMapping
  public Flux<MateriaResponse> listar(@RequestParam(value = "estado", required = false) String estado) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/materias", "read", "BACKEND");
            return estado;
        })
        .flatMapMany(service::listar);
  }

  @GetMapping("/{codigo}")
  public Mono<MateriaResponse> obtener(@PathVariable String codigo) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/materias", "read", "BACKEND");
            return codigo;
        })
        .flatMap(service::obtener)
        .map(MateriaMapper::toResponse);
  }

  @PutMapping("/{codigo}")
  public Mono<ResponseEntity<MateriaResponse>> actualizar(
      @PathVariable String codigo,
      @Valid @RequestBody MateriaUpdateRequest req) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/materias", "update", "BACKEND");
            return codigo;
        })
        .flatMap(id -> service.actualizar(id, req))
        .map(MateriaMapper::toResponse)
        .map(ResponseEntity::ok);
  }

  @PatchMapping("/{codigo}/estado")
  public Mono<ResponseEntity<Void>> cambiarEstado(
      @PathVariable String codigo,
      @Valid @RequestBody MateriaEstadoRequest req) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> {
            String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
            accessControlService.requireAccess(role, "/api/v1/materias", "update_estado", "BACKEND");
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
            accessControlService.requireAccess(role, "/api/v1/materias", "delete", "BACKEND");
            return codigo;
        })
        .flatMap(service::eliminar)
        .thenReturn(ResponseEntity.noContent().build());
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateKeyException.class)
  public void onDuplicate() { /* 409 sin body */ }
}

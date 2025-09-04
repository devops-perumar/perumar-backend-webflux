package pe.edu.perumar.perumar_backend.academico.ciclos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import pe.edu.perumar.perumar_backend.academico.ciclos.dto.CicloRequest;
import pe.edu.perumar.perumar_backend.academico.ciclos.dto.CicloResponse;
import pe.edu.perumar.perumar_backend.academico.ciclos.service.CicloService;
import pe.edu.perumar.perumar_backend.acl.AccessControlService;
import pe.edu.perumar.perumar_backend.config.SecurityUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ciclos")
public class CicloController {

    private final CicloService service;
    private final AccessControlService accessControlService;

    public CicloController(CicloService service, AccessControlService accessControlService) {
        this.service = service;
        this.accessControlService = accessControlService;
    }

    @PostMapping
    public Mono<ResponseEntity<CicloResponse>> crear(@Valid @RequestBody CicloRequest req) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                return accessControlService.requireAccess(role, "/api/v1/ciclos", "create", "BACKEND")
                    .thenReturn(req);
            })
            .flatMap(service::crear)
            .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping("/{id}")
    public Mono<CicloResponse> obtener(@PathVariable String id) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                return accessControlService.requireAccess(role, "/api/v1/ciclos", "read", "BACKEND")
                    .thenReturn(id);
            })
            .flatMap(service::obtener);
    }

    @GetMapping
    public Flux<CicloResponse> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String codigoCarrera) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMapMany(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                return accessControlService.requireAccess(role, "/api/v1/ciclos", "read", "BACKEND")
                    .thenMany(Flux.defer(() -> {
                        if (codigoCarrera != null) return service.listarPorCarrera(codigoCarrera);
                        if (estado != null) return service.listarPorEstado(estado);
                        return service.listarPorEstado("ACTIVO"); // default
                    }));
            });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<CicloResponse>> actualizar(
            @PathVariable String id,
            @Valid @RequestBody CicloRequest req,
            @RequestHeader(name = "X-Tiene-Matriculas", defaultValue = "false") boolean tieneMatriculas,
            @RequestHeader(name = "X-Es-Director", defaultValue = "false") boolean esDirector) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                return accessControlService.requireAccess(role, "/api/v1/ciclos", "edit", "BACKEND")
                    .thenReturn(req);
            })
            .flatMap(r -> service.actualizar(id, r, tieneMatriculas, esDirector))
            .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/estado")
    public Mono<ResponseEntity<Void>> cambiarEstado(@PathVariable String id, @RequestParam String estado) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                return accessControlService.requireAccess(role, "/api/v1/ciclos", "edit_estado", "BACKEND")
                    .thenReturn(id);
            })
            .flatMap(codigo -> service.cambiarEstado(codigo, estado))
            .thenReturn(ResponseEntity.noContent().build());
    }
}

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
            .map(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                accessControlService.requireAccess(role, "/api/v1/ciclos", "create", "BACKEND");
                return req;
            })
            .flatMap(service::crear)
            .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping("/{id}")
    public Mono<CicloResponse> obtener(@PathVariable String id) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                accessControlService.requireAccess(role, "/api/v1/ciclos", "read", "BACKEND");
                return id;
            })
            .flatMap(service::obtener);
    }

    @GetMapping
    public Flux<CicloResponse> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String codigoCarrera) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                accessControlService.requireAccess(role, "/api/v1/ciclos", "read", "BACKEND");
                return new String[]{estado, codigoCarrera};
            })
            .flatMapMany(params -> {
                String estadoParam = params[0];
                String codigoCarreraParam = params[1];

                if (codigoCarreraParam != null) return service.listarPorCarrera(codigoCarreraParam);
                if (estadoParam != null) return service.listarPorEstado(estadoParam);
                return service.listarPorEstado("ACTIVO"); // default
            });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<CicloResponse>> actualizar(
            @PathVariable String id,
            @Valid @RequestBody CicloRequest req,
            @RequestHeader(name = "X-Tiene-Matriculas", defaultValue = "false") boolean tieneMatriculas,
            @RequestHeader(name = "X-Es-Director", defaultValue = "false") boolean esDirector) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                accessControlService.requireAccess(role, "/api/v1/ciclos", "update", "BACKEND");
                return req;
            })
            .flatMap(r -> service.actualizar(id, r, tieneMatriculas, esDirector))
            .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/estado")
    public Mono<ResponseEntity<Void>> cambiarEstado(@PathVariable String id, @RequestParam String estado) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> {
                String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                accessControlService.requireAccess(role, "/api/v1/ciclos", "update", "BACKEND");
                return id;
            })
            .flatMap(codigo -> service.cambiarEstado(codigo, estado))
            .thenReturn(ResponseEntity.noContent().build());
    }
}

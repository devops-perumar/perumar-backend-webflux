package pe.edu.perumar.perumar_backend.academico.carreras;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraResponse;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraUpdateRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.CarreraService.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/carreras")
@Validated
public class CarreraController {

    private final CarreraService service;

    public CarreraController(CarreraService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
    public Mono<ResponseEntity<CarreraResponse>> crear(@Valid @RequestBody CarreraRequest req) {
        return service.crear(req)
            .map(CarreraMapper::toResponse)
            .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR','COORDINADOR')")
    public Flux<CarreraResponse> listar(@RequestParam(value = "estado", required = false) String estado) {
        return service.listar(estado).map(CarreraMapper::toResponse);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR','COORDINADOR')")
    public Mono<CarreraResponse> obtener(@PathVariable String codigo) {
        return service.obtener(codigo).map(CarreraMapper::toResponse);
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
    public Mono<ResponseEntity<CarreraResponse>> actualizar(
            @PathVariable String codigo,
            @Valid @RequestBody CarreraUpdateRequest req) {
        return service.actualizar(codigo, req)
            .map(CarreraMapper::toResponse)
            .map(ResponseEntity::ok);
    }

    @PatchMapping("/{codigo}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
    public Mono<ResponseEntity<Void>> cambiarEstado(
            @PathVariable String codigo,
            @Valid @RequestBody CarreraEstadoRequest req) {
        return service.cambiarEstado(codigo, req)
            .thenReturn(ResponseEntity.noContent().build());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateKeyException.class)
    public void onDuplicate() { }
}

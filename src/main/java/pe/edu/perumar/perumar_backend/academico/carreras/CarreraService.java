package pe.edu.perumar.perumar_backend.academico.carreras;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraRequest;
import pe.edu.perumar.perumar_backend.academico.carreras.dto.CarreraUpdateRequest;
import pe.edu.perumar.perumar_backend.academico.materias.MateriaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CarreraService {

    private final CarreraRepository repo;
    private final MateriaRepository materiaRepo;

    public CarreraService(CarreraRepository repo, MateriaRepository materiaRepo) {
        this.repo = repo;
        this.materiaRepo = materiaRepo;
    }

    public Mono<Carrera> crear(CarreraRequest req) {
        if (!StringUtils.hasText(req.getCodigo())) {
            return Mono.error(new IllegalArgumentException("codigo requerido"));
        }

        return repo.findByCodigo(req.getCodigo())
            .flatMap(existing -> Mono.<Carrera>error(new DuplicateKeyException("Carrera ya existe: " + req.getCodigo())))
            .switchIfEmpty(Mono.defer(() ->
                validarMaterias(req.getMaterias())
                    .then(Mono.fromSupplier(() -> {
                        Carrera c = CarreraMapper.toEntityOnCreate(req);
                        return c;
                    }))
                    .flatMap(repo::save)
            ));
    }

    public Flux<Carrera> listar(String estado) {
        return repo.findAll(estado);
    }

    public Mono<Carrera> obtener(String codigo) {
        return repo.findByCodigo(codigo);
    }

    public Mono<Carrera> actualizar(String codigo, CarreraUpdateRequest req) {
        return repo.findByCodigo(codigo)
            .flatMap(actual -> validarMaterias(req.getMaterias())
                .then(Mono.fromSupplier(() -> {
                    CarreraMapper.applyUpdate(actual, req);
                    return actual;
                }))
                .flatMap(repo::update)
            );
    }

    public Mono<Void> cambiarEstado(String codigo, CarreraEstadoRequest req) {
        return repo.updateEstado(codigo, req.getEstado());
    }

    private Mono<Void> validarMaterias(java.util.List<String> codigosMaterias) {
        if (codigosMaterias == null || codigosMaterias.isEmpty()) {
            return Mono.empty();
        }
        return Flux.fromIterable(codigosMaterias)
            .flatMap(cod -> materiaRepo.findByCodigo(cod)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Materia no existe: " + cod))))
            .then();
    }

    public static class DuplicateKeyException extends RuntimeException {
        public DuplicateKeyException(String msg) { super(msg); }
    }
}

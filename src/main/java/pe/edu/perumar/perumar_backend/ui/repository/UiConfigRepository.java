package pe.edu.perumar.perumar_backend.ui.repository;

import pe.edu.perumar.perumar_backend.ui.model.UiMenuConfigEntity;
import reactor.core.publisher.Mono;

public interface UiConfigRepository {
    Mono<UiMenuConfigEntity> findByRole(String role);
}

package pe.edu.perumar.perumar_backend.ui.service;

import pe.edu.perumar.perumar_backend.ui.model.UiMenuConfigEntity;
import reactor.core.publisher.Mono;

public interface UiConfigService {
    Mono<UiMenuConfigEntity> getMenuForCurrentUser();
}

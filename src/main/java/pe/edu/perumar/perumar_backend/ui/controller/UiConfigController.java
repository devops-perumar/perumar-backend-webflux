package pe.edu.perumar.perumar_backend.ui.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.perumar.perumar_backend.ui.model.UiMenuConfigEntity;
import pe.edu.perumar.perumar_backend.ui.service.UiConfigService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ui")
public class UiConfigController {

    private final UiConfigService uiConfigService;

    public UiConfigController(UiConfigService uiConfigService) {
        this.uiConfigService = uiConfigService;
    }

    /** Devuelve la configuración de menú según el rol del JWT (no del cliente). */
    @GetMapping(value = "/config")
    @PreAuthorize("isAuthenticated()") // si tienes @EnableMethodSecurity
    public Mono<UiMenuConfigEntity> getConfig() {
        return uiConfigService.getMenuForCurrentUser();
    }
}

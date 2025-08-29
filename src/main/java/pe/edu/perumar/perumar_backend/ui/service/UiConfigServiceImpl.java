package pe.edu.perumar.perumar_backend.ui.service;

import java.util.Collections;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;


import org.springframework.security.core.context.SecurityContext;
import pe.edu.perumar.perumar_backend.ui.model.UiMenuConfigEntity;
import pe.edu.perumar.perumar_backend.ui.model.UiMenuItem;
import pe.edu.perumar.perumar_backend.ui.model.UiMenuSection;
import pe.edu.perumar.perumar_backend.ui.repository.UiConfigRepository;
import reactor.core.publisher.Mono;

@Service
public class UiConfigServiceImpl implements UiConfigService {

    private static final Logger log = LoggerFactory.getLogger(UiConfigServiceImpl.class);
    private final UiConfigRepository repository;

    public UiConfigServiceImpl(UiConfigRepository repository) {
        this.repository = repository;
    }


    @Override
    public Mono<UiMenuConfigEntity> getMenuForCurrentUser() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(this::extractRoleFromAuth)             // ← obtiene DIRECTOR/ADMIN/etc
        .defaultIfEmpty("GUEST")
        .flatMap(role ->
            repository.findByRole(role)
                /*.doOnNext(cfg -> log.info("ui-config found role={} sections={}", role,
                    cfg.getSections() != null ? cfg.getSections().size() : 0))*/
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("ui-config NOT FOUND for role={}, returning fallback", role);
                    return Mono.just(buildFallback(role));
                }))
        )
        .onErrorResume(e -> {
            log.error("Fallo al obtener menú desde DynamoDB, usando fallback", e);
            return Mono.just(buildFallback("GUEST"));
        });
    }

    private String extractRoleFromAuth(Authentication auth) {
    if (auth instanceof JwtAuthenticationToken jwtAuth) {
        var jwt = jwtAuth.getToken();
        var groups = jwt.getClaimAsStringList("cognito:groups");
        if (groups != null && !groups.isEmpty()) return normalize(groups.get(0));
        var customRole = jwt.getClaimAsString("custom:role");
        if (customRole != null && !customRole.isBlank()) return normalize(customRole);
        var role = jwt.getClaimAsString("role");
        if (role != null && !role.isBlank()) return normalize(role);
    }
    return "GUEST";
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    /** Menú mínimo seguro si no existe config en DynamoDB */
    private UiMenuConfigEntity buildFallback(String role) {
        UiMenuItem logout = new UiMenuItem();
        logout.setLabel("Cerrar sesión");
        logout.setPath("/logout");
        logout.setIcon("🚪");
        logout.setPerm(null);

        UiMenuSection cuenta = new UiMenuSection();
        cuenta.setLabel("Cuenta");
        cuenta.setItems(Collections.singletonList(logout));

        UiMenuConfigEntity cfg = new UiMenuConfigEntity();
        cfg.setPk("ROLE#" + normalize(role));     // 🆗 Añadir PK
        cfg.setSk("MENU#v1");                      // 🆗 Añadir SK
        cfg.setRole(normalize(role));
        cfg.setSections(Collections.singletonList(cuenta));
        cfg.setUpdatedAt(null);
        return cfg;
    }

}

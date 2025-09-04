package pe.edu.perumar.perumar_backend.acl;

import java.util.function.Supplier;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import pe.edu.perumar.perumar_backend.config.SecurityUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Utility component to centralize ACL checks while preserving the reactive
 * security context. It wraps {@link AccessControlService#requireAccess} and
 * executes a supplier only when access is granted.
 */
@Component
public class AccessGuard {

    private static final String DEFAULT_SCOPE = "BACKEND";

    private final AccessControlService accessControlService;

    public AccessGuard(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    public <T> Mono<T> requireMono(String resource, String action, Supplier<Mono<T>> next) {
        return requireMono(resource, action, DEFAULT_SCOPE, next);
    }

    public <T> Mono<T> requireMono(String resource, String action, String scope, Supplier<Mono<T>> next) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                    accessControlService.requireAccess(role, resource, action, scope);
                    return next.get();
                });
    }

    public <T> Flux<T> requireFlux(String resource, String action, Supplier<Flux<T>> next) {
        return requireFlux(resource, action, DEFAULT_SCOPE, next);
    }

    public <T> Flux<T> requireFlux(String resource, String action, String scope, Supplier<Flux<T>> next) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMapMany(ctx -> {
                    String role = SecurityUtils.extractUserRole(ctx.getAuthentication());
                    accessControlService.requireAccess(role, resource, action, scope);
                    return next.get();
                });
    }
}

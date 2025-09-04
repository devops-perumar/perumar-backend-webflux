package pe.edu.perumar.perumar_backend.acl;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccessControlService {

    private final AclDynamoRepository aclDynamoRepository;

    public AccessControlService(AclDynamoRepository aclDynamoRepository) {
        this.aclDynamoRepository = aclDynamoRepository;
    }

    /**
     * Verifica si un rol tiene acceso.
     * @return Mono que emite true si tiene acceso, false si no
     */
    public Mono<Boolean> checkAccess(String role, String resource, String action, String scope) {
        return aclDynamoRepository.hasAccess(role, resource, action, scope);
    }

    /**
     * Lanza excepción si no tiene acceso.
     */
    public Mono<Void> requireAccess(String role, String resource, String action, String scope) {
        return checkAccess(role, resource, action, scope)
                .flatMap(allowed -> {
                    if (Boolean.TRUE.equals(allowed)) {
                        return Mono.empty();
                    }
                    return Mono.error(new AccessDeniedException("Acceso denegado para " + role + " a " + resource + "#" + action + "#" + scope));
                });
    }
}

package pe.edu.perumar.perumar_backend.acl;

import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    private final AclDynamoRepository aclDynamoRepository;

    public AccessControlService(AclDynamoRepository aclDynamoRepository) {
        this.aclDynamoRepository = aclDynamoRepository;
    }

    /**
     * Verifica si un rol tiene acceso.
     * @return true si tiene acceso, false si no
     */
    public boolean checkAccess(String role, String resource, String action, String scope) {
        return aclDynamoRepository.hasAccess(role, resource, action, scope);
    }

    /**
     * Lanza excepción si no tiene acceso.
     */
    public void requireAccess(String role, String resource, String action, String scope) {
        boolean allowed = checkAccess(role, resource, action, scope);
        if (!allowed) {
            throw new AccessDeniedException("Acceso denegado para " + role + " a " + resource + "#" + action + "#" + scope);
        }
    }
}

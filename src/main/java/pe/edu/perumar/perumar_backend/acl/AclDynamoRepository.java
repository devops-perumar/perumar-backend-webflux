package pe.edu.perumar.perumar_backend.acl;

public interface AclDynamoRepository {
    /**
     * Verifica si un rol tiene acceso a un recurso con acción y ámbito dados.
     * @param role     Rol del usuario (ej. ADMIN, DIRECTOR)
     * @param resource Recurso (ej. /api/v1/materias, MATERIA_FORM)
     * @param action   Acción (create, edit, list, view, etc.)
     * @param scope    BACKEND o FRONTEND
     * @return true si tiene permiso, false si no tiene o no existe el registro
     */
    boolean hasAccess(String role, String resource, String action, String scope);
}

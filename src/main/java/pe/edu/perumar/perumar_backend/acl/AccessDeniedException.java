package pe.edu.perumar.perumar_backend.acl;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}

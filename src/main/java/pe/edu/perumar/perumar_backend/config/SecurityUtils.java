package pe.edu.perumar.perumar_backend.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;

public class SecurityUtils {

    public static String extractUserRole(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        List<String> groups = jwt.getClaimAsStringList("cognito:groups");
        return (groups != null && !groups.isEmpty()) ? groups.get(0) : null;
    }
}

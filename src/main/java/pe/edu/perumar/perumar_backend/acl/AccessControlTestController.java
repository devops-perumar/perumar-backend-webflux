package pe.edu.perumar.perumar_backend.acl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
public class AccessControlTestController {

    private final AccessControlService accessControlService;

    public AccessControlTestController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping("/acl/secure-test")
    public Mono<String> secureTest(
            @RequestParam String role,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam String scope
    ) {
        return accessControlService.requireAccess(role, resource, action, scope)
                .thenReturn("✅ Acceso permitido");
    }
}

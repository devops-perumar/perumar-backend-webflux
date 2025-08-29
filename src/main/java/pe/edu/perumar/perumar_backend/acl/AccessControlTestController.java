package pe.edu.perumar.perumar_backend.acl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AccessControlTestController {

    private final AccessControlService accessControlService;

    public AccessControlTestController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping("/acl/secure-test")
    public String secureTest(
            @RequestParam String role,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam String scope
    ) {
        accessControlService.requireAccess(role, resource, action, scope);
        return "✅ Acceso permitido";
    }
}

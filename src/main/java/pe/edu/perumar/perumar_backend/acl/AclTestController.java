package pe.edu.perumar.perumar_backend.acl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AclTestController {

    private final AclDynamoRepository aclDynamoRepository;

    public AclTestController(AclDynamoRepository aclDynamoRepository) {
        this.aclDynamoRepository = aclDynamoRepository;
    }

    @GetMapping("/acl/test")
    public boolean testAcl(
            @RequestParam String role,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam String scope
    ) {
        return aclDynamoRepository.hasAccess(role, resource, action, scope);
    }
}

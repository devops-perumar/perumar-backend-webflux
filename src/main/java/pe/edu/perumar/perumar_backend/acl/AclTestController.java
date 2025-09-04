package pe.edu.perumar.perumar_backend.acl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AclTestController {

    private final AclDynamoRepository aclDynamoRepository;

    public AclTestController(AclDynamoRepository aclDynamoRepository) {
        this.aclDynamoRepository = aclDynamoRepository;
    }

    @GetMapping("/acl/test")
    public Mono<Boolean> testAcl(
            @RequestParam String role,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam String scope
    ) {
        return aclDynamoRepository.hasAccess(role, resource, action, scope);
    }
}

// src/main/java/pe/edu/perumar/perumar_backend/acl/AclController.java
package pe.edu.perumar.perumar_backend.acl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/acl")
@RequiredArgsConstructor
public class AclController {

    private final AclService service;

    @GetMapping("/me")
    public Mono<AclMeResponse> me(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) return Mono.error(new RuntimeException("Unauthorized"));

        String sub = jwt.getSubject();
        List<String> groups = Optional.ofNullable(jwt.getClaimAsStringList("cognito:groups")).orElse(List.of());
        List<String> customPerms = Optional.ofNullable(jwt.getClaimAsStringList("custom:perms")).orElse(List.of());

        return service.resolvePerms(sub, groups, customPerms)
                .map(AclMeResponse::new);
    }
}


// src/main/java/pe/edu/perumar/perumar_backend/audit/AuditController.java
package pe.edu.perumar.perumar_backend.audit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService service;

    @PostMapping("/menu-click")
    public Mono<ResponseEntity<Void>> menuClick(@AuthenticationPrincipal Jwt jwt,
                                                @Valid @RequestBody MenuClickRequest req,
                                                @RequestHeader(value = "X-Forwarded-For", required = false) String xff,
                                                @RequestHeader(value = "User-Agent", required = false) String ua) {
        if (jwt == null) return Mono.just(ResponseEntity.status(401).build());
        String sub = jwt.getSubject();
        List<String> groups = Optional.ofNullable(jwt.getClaimAsStringList("cognito:groups")).orElse(List.of());
        String ip = (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : null;

        return service.record(sub, groups, req, ip, ua)
                .thenReturn(ResponseEntity.accepted().build()); // 202
    }
}

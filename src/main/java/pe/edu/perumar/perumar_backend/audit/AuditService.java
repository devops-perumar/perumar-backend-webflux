// src/main/java/pe/edu/perumar/perumar_backend/audit/AuditService.java
package pe.edu.perumar.perumar_backend.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j
public class AuditService {
    private final AuditRepository repo;

    public Mono<Void> record(String sub, List<String> groups, MenuClickRequest req,
                             String ip, String userAgent) {

        long now = (req.ts() != null ? req.ts() : Instant.now().toEpochMilli());
        String role = groups != null && !groups.isEmpty() ? groups.get(0) : null;

        // Calcular TTL (90 días desde ahora, en epoch seconds)
        long ttl = Instant.now().plus(90, ChronoUnit.DAYS).getEpochSecond();
        
        // Log a CloudWatch siempre (Operacional)
        log.info("audit.menu_click userSub={} role={} section={} item={} path={} ts={}",
                sub, role, req.section(), req.item(), req.path(), now);

        // Intento de persistencia (fiabilidad: no bloquea la respuesta)
        AuditMenuClickItem dto = AuditMenuClickItem.builder()
                .pk("user#" + sub)
                .sk("ts#" + now + "#" + UUID.randomUUID())
                .role(role)
                .section(req.section())
                .item(req.item())
                .path(req.path())
                .ts(now)
                .ip(ip)
                .userAgent(userAgent != null && userAgent.length() > 180 ? userAgent.substring(0,180) : userAgent)
                .ttl(ttl)
                .build();

        return repo.save(dto).onErrorResume(e -> {
            log.warn("audit.persist.failed: {}", e.toString());
            return Mono.empty();
        });
    }
}

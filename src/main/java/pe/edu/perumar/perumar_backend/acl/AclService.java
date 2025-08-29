// src/main/java/pe/edu/perumar/perumar_backend/acl/AclService.java
package pe.edu.perumar.perumar_backend.acl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

// AclService.java
@Service
@RequiredArgsConstructor
public class AclService {
  private final AccessControlRepository repo;

  public Mono<Set<String>> resolvePerms(String sub, List<String> groups, Collection<String> jwtPerms) {
    LinkedHashSet<String> pkValues = new LinkedHashSet<>();
    // Roles tal cual en la tabla (ajusta si usaste otro casing/prefijo):
    for (String g : groups) pkValues.add(g);
    // Si también tienes filas por usuario en la MISMA PK:
    // pkValues.add("user#" + sub);

    Mono<Set<String>> dyn = Flux.fromIterable(pkValues)
        .flatMap(repo::findByRole)
        .filter(item -> Boolean.TRUE.equals(item.getAllow()))   // 👈 filtra solo los permitidos
        .map(AccessControlItem::getResourceActionScope) // 👈 usa el SK real
        .collect(Collectors.toCollection(LinkedHashSet::new));

    Set<String> jwtSet = jwtPerms == null ? Set.of() : new LinkedHashSet<>(jwtPerms);
    return dyn.map(d -> { var out = new LinkedHashSet<>(jwtSet); out.addAll(d); return out; });
  }
}


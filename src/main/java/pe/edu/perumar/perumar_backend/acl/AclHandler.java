// src/main/java/pe/edu/perumar/perumar_backend/acl/AclHandler.java
package pe.edu.perumar.perumar_backend.acl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AclHandler {

    private final AclService service;

    public Mono<ServerResponse> me(ServerRequest req) {
        return req.exchange().getPrincipal()
                .cast(Authentication.class)
                .flatMap(auth -> {
                    Object principal = auth.getPrincipal();
                    if (!(principal instanceof Jwt jwt)) {
                        return ServerResponse.status(401).build();
                    }

                    String sub = jwt.getSubject(); // sub
                    // groups
                    List<String> groups = Optional.ofNullable(jwt.getClaimAsStringList("cognito:groups"))
                            .orElseGet(List::of);
                    // custom perms (si los usas)
                    List<String> customPerms = Optional.ofNullable(jwt.getClaimAsStringList("custom:perms"))
                            .orElseGet(List::of);

                    return service.resolvePerms(sub, groups, customPerms)
                            .flatMap(perms -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(new AclMeResponse(perms)));
                });
    }
}

package pe.edu.perumar.perumar_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/v1/public/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
            )
            .build();
    }

    /**
     * Convierte el claim "cognito:groups" en authorities ROLE_*
     * y también mapea los scopes de OAuth2 a SCOPE_* (por si los usas).
     */
    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthConverter() {
        JwtAuthenticationConverter delegate = new JwtAuthenticationConverter();
        delegate.setJwtGrantedAuthoritiesConverter(jwt -> extractAuthorities(jwt));
        return new ReactiveJwtAuthenticationConverterAdapter(delegate);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // 1) Grupos de Cognito -> ROLE_*
        List<String> groups = jwt.getClaimAsStringList("cognito:groups");
        List<GrantedAuthority> groupAuthorities = groups == null ? List.of() :
            groups.stream()
                  .map(g -> new SimpleGrantedAuthority("ROLE_" + g))
                  .collect(Collectors.toList());

        // 2) Scopes -> SCOPE_*
        List<String> scopes = jwt.getClaimAsStringList("scope"); // a veces viene como string con espacios
        if (scopes == null) {
            String scopeStr = jwt.getClaimAsString("scope");
            if (scopeStr != null) {
                scopes = List.of(scopeStr.split(" "));
            }
        }
        List<GrantedAuthority> scopeAuthorities = scopes == null ? List.of() :
            scopes.stream()
                  .map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
                  .collect(Collectors.toList());

        return new java.util.ArrayList<GrantedAuthority>() {{
            addAll(groupAuthorities);
            addAll(scopeAuthorities);
        }};
    }
}

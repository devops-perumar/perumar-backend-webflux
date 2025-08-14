// src/test/java/pe/edu/perumar/perumar_backend/TestSecurityConfig.java
package pe.edu.perumar.perumar_backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
@Profile("test")
public class TestSecurityConfig {
  @Bean
  SecurityWebFilterChain testFilterChain(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
               .authorizeExchange(ex -> ex.anyExchange().permitAll())
               .build();
  }
}

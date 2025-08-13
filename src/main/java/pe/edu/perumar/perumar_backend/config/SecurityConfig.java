// src/main/java/pe/edu/perumar/perumar_backend/config/SecurityConfig.java
package pe.edu.perumar.perumar_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;

import java.util.List;

@Profile("!test")
@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

  // === Ajusta si cambias de región o pool ===
  private static final String REGION = "us-east-1";
  private static final String USER_POOL_ID = "us-east-1_MAWJfjc64";

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    // En prod, reemplaza por tu dominio de CloudFront (p.ej. https://app.perumar.edu.pe)
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
    config.setAllowedHeaders(List.of("Authorization","Content-Type","x-requested-with"));
    // OJO: allowCredentials=true no es compatible con AllowedOrigins="*"
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  /** Convierte cognito:groups -> ROLE_* para que hasAnyRole funcione */
  @Bean
  public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grants = new JwtGrantedAuthoritiesConverter();
    // Prefijo requerido por Spring para .hasRole/.hasAnyRole
    grants.setAuthorityPrefix("ROLE_");
    // Toma los grupos desde el claim de Cognito
    grants.setAuthoritiesClaimName("cognito:groups");

    JwtAuthenticationConverter base = new JwtAuthenticationConverter();
    base.setJwtGrantedAuthoritiesConverter(grants);
    // Adaptador reactivo para WebFlux
    return new ReactiveJwtAuthenticationConverterAdapter(base);
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                          ReactiveJwtAuthenticationConverterAdapter converter) {
    http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .cors(c -> c.configurationSource(corsConfigurationSource()))
      .authorizeExchange(ex -> ex
        // Swagger/Docs/Actuator públicos (ajústalo si quieres protegerlos)
        .pathMatchers(HttpMethod.GET,
          "/actuator/**",
          "/swagger-ui.html", "/swagger-ui/**",
          "/api-docs/**", "/api-docs/swagger-config",
          "/v3/api-docs/**", "/v3/api-docs/swagger-config",
          "/webjars/**"
        ).permitAll()
        // Preflight CORS
        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // Materias — reglas por rol (Fase 1)
        .pathMatchers(HttpMethod.POST,   "/api/v1/materias/**").hasAnyRole("ADMIN","DIRECTOR")
        .pathMatchers(HttpMethod.PUT,    "/api/v1/materias/**").hasAnyRole("ADMIN","DIRECTOR")
        .pathMatchers(HttpMethod.PATCH,  "/api/v1/materias/**").hasAnyRole("ADMIN","DIRECTOR")
        .pathMatchers(HttpMethod.DELETE, "/api/v1/materias/**").hasAnyRole("ADMIN","DIRECTOR")
        .pathMatchers(HttpMethod.GET,    "/api/v1/materias/**").hasAnyRole("ADMIN","DIRECTOR","COORDINADOR")

        // Futuras rutas (Carreras/Ciclos) aquí…

        // Resto autenticado
        .anyExchange().authenticated()
      )
      .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(jwt -> jwt
          .jwtDecoder(jwtDecoder())
          .jwtAuthenticationConverter(converter)
        )
      );

    return http.build();
  }

  /** Valida firma e issuer del JWT de Cognito */
  @Bean
  public ReactiveJwtDecoder jwtDecoder() {
    String issuer = "https://cognito-idp." + REGION + ".amazonaws.com/" + USER_POOL_ID;
    return ReactiveJwtDecoders.fromIssuerLocation(issuer);
  }
}

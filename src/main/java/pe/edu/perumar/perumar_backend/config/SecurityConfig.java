// src/main/java/pe/edu/perumar/perumar_backend/config/SecurityConfig.java
package pe.edu.perumar.perumar_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;

import java.util.List;

@Configuration
public class SecurityConfig {

    // Ajusta estos valores según tu User Pool
    private static final String REGION = "us-east-2";
    private static final String USER_POOL_ID = "us-east-2_A7iZK6M18";
    

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeExchange(exchanges -> exchanges
            // Permitir GET para Swagger y sus configuraciones remotas
            .pathMatchers(HttpMethod.GET,
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/api-docs/**",
                "/api-docs/swagger-config",
                "/v3/api-docs/**",
                "/v3/api-docs/swagger-config",
                "/webjars/**"
            ).permitAll()
            // Opciones CORS
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            // El resto sigue autenticado
            .anyExchange().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(org.springframework.security.config.Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String issuer = "https://cognito-idp." + REGION + ".amazonaws.com/" + USER_POOL_ID;
        return ReactiveJwtDecoders.fromIssuerLocation(issuer);
    }
}

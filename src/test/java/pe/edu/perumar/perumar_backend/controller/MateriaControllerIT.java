package pe.edu.perumar.perumar_backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.perumar.perumar_backend.dto.MateriaEstadoRequest;
import pe.edu.perumar.perumar_backend.model.Materia;
import pe.edu.perumar.perumar_backend.repository.MateriaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class MateriaControllerIT {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  MateriaRepository repo;

  @Test
  void post_crear_201() {
    // Arrange
    when(repo.findByCodigo("MAT001")).thenReturn(Mono.empty());
    when(repo.save(any(Materia.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    // Act + Assert
    webTestClient.post()
      .uri("/api/v1/materias")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {"codigo":"MAT001","nombre":"Matemáticas I","descripcion":"Base"}
      """)
      .exchange()
      .expectStatus().isCreated()
      .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.codigo").isEqualTo("MAT001")
      .jsonPath("$.estado").isEqualTo("ACTIVO");
  }

  @Test
  void get_listar_200() {
    Materia m = new Materia();
    m.setCodigo("MAT001");
    m.setNombre("Matemáticas I");
    m.setEstado("ACTIVO");
    m.setCreatedAt(Instant.now());
    m.setUpdatedAt(Instant.now());

    when(repo.findAll(null)).thenReturn(Flux.just(m));

    webTestClient.get()
      .uri("/api/v1/materias")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$[0].codigo").isEqualTo("MAT001")
      .jsonPath("$[0].estado").isEqualTo("ACTIVO");
  }

  @Test
  void put_actualizar_200() {
    Materia actual = new Materia();
    actual.setCodigo("MAT001");
    actual.setNombre("Old");
    actual.setDescripcion("Old");
    actual.setEstado("ACTIVO");
    actual.setCreatedAt(Instant.now());
    actual.setUpdatedAt(Instant.now());

    when(repo.findByCodigo("MAT001")).thenReturn(Mono.just(actual));
    when(repo.update(any(Materia.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    webTestClient.put()
      .uri("/api/v1/materias/MAT001")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {"nombre":"Nuevo","descripcion":"Nueva desc"}
      """)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.nombre").isEqualTo("Nuevo")
      .jsonPath("$.descripcion").isEqualTo("Nueva desc");
  }

  @Test
  void patch_cambiarEstado_204() {
    when(repo.updateEstado(eq("MAT001"), eq("INACTIVO"))).thenReturn(Mono.empty());

    webTestClient.patch()
      .uri("/api/v1/materias/MAT001/estado")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {"estado":"INACTIVO"}
      """)
      .exchange()
      .expectStatus().isNoContent();
  }

  // --- Configuración de seguridad para tests: permitir todo ---
  @TestConfiguration
  static class TestSecurityConfig {
    @Bean
    SecurityWebFilterChain testSpringSecurityFilterChain(ServerHttpSecurity http) {
      return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(ex -> ex.anyExchange().permitAll())
        .build();
    }
  }
}

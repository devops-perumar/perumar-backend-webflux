package pe.edu.perumar.perumar_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.perumar.perumar_backend.model.Carrera;
import pe.edu.perumar.perumar_backend.model.ModalidadCarrera;
import pe.edu.perumar.perumar_backend.model.Materia;
import pe.edu.perumar.perumar_backend.repository.CarreraRepository;
import pe.edu.perumar.perumar_backend.repository.MateriaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class CarreraControllerIT {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  CarreraRepository carreraRepo;

  @MockBean
  MateriaRepository materiaRepo;

  @Test
  void post_crear_201() {
    // Validación: carrera no existe y materias válidas
    when(carreraRepo.findByCodigo("CAR001")).thenReturn(Mono.empty());
    when(materiaRepo.findByCodigo("MAT001")).thenReturn(Mono.just(new Materia()));
    when(materiaRepo.findByCodigo("MAT002")).thenReturn(Mono.just(new Materia()));
    // Save devuelve el mismo objeto
    when(carreraRepo.save(any(Carrera.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    webTestClient.post()
      .uri("/api/v1/carreras")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {
          "codigo":"CAR001",
          "nombre":"Marinería",
          "descripcion":"Formación básica",
          "modalidad":"SIN_EXPERIENCIA",
          "materias":["MAT001","MAT002"]
        }
      """)
      .exchange()
      .expectStatus().isCreated()
      .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.codigo").isEqualTo("CAR001")
      .jsonPath("$.modalidad").isEqualTo("SIN_EXPERIENCIA")
      .jsonPath("$.materias.length()").isEqualTo(2);
  }

  @Test
  void get_listar_200() {
    Carrera c = new Carrera();
    c.setCodigo("CAR001");
    c.setNombre("Marinería");
    c.setDescripcion("Formación básica");
    c.setModalidad(ModalidadCarrera.SIN_EXPERIENCIA);
    c.setMaterias(List.of("MAT001","MAT002"));
    c.setEstado("ACTIVO");
    c.setCreatedAt(Instant.now());
    c.setUpdatedAt(Instant.now());

    when(carreraRepo.findAll(null)).thenReturn(Flux.just(c));

    webTestClient.get()
      .uri("/api/v1/carreras")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$[0].codigo").isEqualTo("CAR001")
      .jsonPath("$[0].estado").isEqualTo("ACTIVO")
      .jsonPath("$[0].materias.length()").isEqualTo(2);
  }

  @Test
  void put_actualizar_200() {
    // Estado actual
    Carrera actual = new Carrera();
    actual.setCodigo("CAR001");
    actual.setNombre("Old");
    actual.setDescripcion("Old");
    actual.setModalidad(ModalidadCarrera.CON_EXPERIENCIA);
    actual.setMaterias(List.of("MAT001"));
    actual.setEstado("ACTIVO");
    actual.setCreatedAt(Instant.now());
    actual.setUpdatedAt(Instant.now());

    when(carreraRepo.findByCodigo("CAR001")).thenReturn(Mono.just(actual));
    // Validación de nuevas materias del request
    when(materiaRepo.findByCodigo("MAT003")).thenReturn(Mono.just(new Materia()));
    when(carreraRepo.update(any(Carrera.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    webTestClient.put()
      .uri("/api/v1/carreras/CAR001")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {
          "nombre":"Nuevo",
          "descripcion":"Nueva desc",
          "modalidad":"SIN_EXPERIENCIA",
          "materias":["MAT003"]
        }
      """)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.nombre").isEqualTo("Nuevo")
      .jsonPath("$.descripcion").isEqualTo("Nueva desc")
      .jsonPath("$.modalidad").isEqualTo("SIN_EXPERIENCIA")
      .jsonPath("$.materias.length()").isEqualTo(1)
      .jsonPath("$.materias[0]").isEqualTo("MAT003");
  }

  @Test
  void patch_cambiarEstado_204() {
    when(carreraRepo.updateEstado(eq("CAR001"), eq("INACTIVO"))).thenReturn(Mono.empty());

    webTestClient.patch()
      .uri("/api/v1/carreras/CAR001/estado")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {"estado":"INACTIVO"}
      """)
      .exchange()
      .expectStatus().isNoContent();
  }

  // --- Seguridad en tests: permitir todo ---
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

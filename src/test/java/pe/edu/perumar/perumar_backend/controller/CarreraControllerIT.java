package pe.edu.perumar.perumar_backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.perumar.perumar_backend.model.Carrera;
import pe.edu.perumar.perumar_backend.model.ModalidadCarrera;
import pe.edu.perumar.perumar_backend.repository.CarreraRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CarreraControllerIT {

  @Autowired WebTestClient webTestClient;
  @MockBean CarreraRepository repo;

  private WebTestClient asDirector() {
    return webTestClient.mutateWith(
      mockJwt().authorities(new SimpleGrantedAuthority("ROLE_DIRECTOR"))
    );
  }

  private Carrera sample() {
    var c = new Carrera();
    c.setCodigo("CAR001");
    c.setNombre("Ingeniería de Sistemas");
    c.setDescripcion("Carrera de TI");
    c.setModalidad(ModalidadCarrera.SIN_EXPERIENCIA); // obligatorio
    c.setMaterias(List.of("MAT001"));  // al menos una materia si es requerido
    c.setEstado("ACTIVO");
    c.setCreatedAt(Instant.now());
    c.setUpdatedAt(Instant.now());
    return c;
  }

  @Test
  void post_crear_201() {
    when(repo.findByCodigo(eq("CAR001"))).thenReturn(Mono.empty());
    when(repo.save(any(Carrera.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0, Carrera.class)));

    asDirector().post().uri("/api/v1/carreras")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {
          "codigo":"CAR001",
          "nombre":"Ingeniería de Sistemas",
          "descripcion":"Carrera de TI",
          "modalidad":"SIN_EXPERIENCIA",
          "materias":["MAT001"]
        }
      """)
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.codigo").isEqualTo("CAR001")
      .jsonPath("$.estado").isEqualTo("ACTIVO");
  }

  @Test
  void get_listar_200() {
    when(repo.findAll(isNull())).thenReturn(Flux.just(sample()));

    asDirector().get().uri("/api/v1/carreras")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$[0].codigo").isEqualTo("CAR001")
      .jsonPath("$[0].estado").isEqualTo("ACTIVO");
  }

  @Test
  void put_actualizar_200() {
    var actual = sample();
    when(repo.findByCodigo(eq("CAR001"))).thenReturn(Mono.just(actual));
    when(repo.update(any(Carrera.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0, Carrera.class)));

    asDirector().put().uri("/api/v1/carreras/CAR001")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {
          "codigo":"CAR001",
          "nombre":"Nuevo nombre",
          "descripcion":"Nueva desc",
          "modalidad":"SIN_EXPERIENCIA",
          "materias":["MAT001"]
        }
      """)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.nombre").isEqualTo("Nuevo nombre")
      .jsonPath("$.descripcion").isEqualTo("Nueva desc");
  }

  @Test
  void patch_cambiarEstado_204() {
    when(repo.updateEstado(eq("CAR001"), eq("INACTIVO"))).thenReturn(Mono.empty());

    asDirector().patch().uri("/api/v1/carreras/CAR001/estado")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {"estado":"INACTIVO"}
      """)
      .exchange()
      .expectStatus().isNoContent();
  }
}

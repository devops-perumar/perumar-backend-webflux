package pe.edu.perumar.perumar_backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.perumar.perumar_backend.BaseIT;
import pe.edu.perumar.perumar_backend.model.Materia;
import pe.edu.perumar.perumar_backend.repository.MateriaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// JWT simulado para pasar la cadena real de seguridad
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class MateriaControllerIT extends BaseIT {

  @Autowired WebTestClient webTestClient;
  @MockBean MateriaRepository repo;

  private WebTestClient asDirector() {
    return webTestClient.mutateWith(
      mockJwt().authorities(new SimpleGrantedAuthority("ROLE_DIRECTOR"))
    );
  }

  @Test
  void post_crear_201() {
    when(repo.findByCodigo(eq("MAT001"))).thenReturn(Mono.empty());
    when(repo.save(any(Materia.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0, Materia.class)));

    asDirector().post().uri("/api/v1/materias")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {"codigo":"MAT001","nombre":"Matemáticas I","descripcion":"Base"}
      """)
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.codigo").isEqualTo("MAT001")
      .jsonPath("$.estado").isEqualTo("ACTIVO");
  }

  @Test
  void get_listar_200() {
    var m = new Materia();
    m.setCodigo("MAT001");
    m.setNombre("Matemáticas I");
    m.setDescripcion("Base");
    m.setEstado("ACTIVO");
    m.setCreatedAt(Instant.now());
    m.setUpdatedAt(Instant.now());

    when(repo.findByEstado("ACTIVO")).thenReturn(Flux.just(m));

    asDirector().get().uri("/api/v1/materias")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$[0].codigo").isEqualTo("MAT001")
      .jsonPath("$[0].estado").isEqualTo("ACTIVO");
  }

  @Test
  void put_actualizar_200() {
    var actual = new Materia();
    actual.setCodigo("MAT001"); actual.setNombre("Old"); actual.setDescripcion("Old");
    actual.setEstado("ACTIVO"); actual.setCreatedAt(Instant.now()); actual.setUpdatedAt(Instant.now());

    when(repo.findByCodigo(eq("MAT001"))).thenReturn(Mono.just(actual));
    when(repo.update(any(Materia.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0, Materia.class)));

    asDirector().put().uri("/api/v1/materias/MAT001")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {"nombre":"Nuevo nombre","descripcion":"Nueva desc"}
      """)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.nombre").isEqualTo("Nuevo nombre")
      .jsonPath("$.descripcion").isEqualTo("Nueva desc");
  }

  @Test
  void patch_cambiarEstado_204() {
    when(repo.updateEstado(eq("MAT001"), eq("INACTIVO"))).thenReturn(Mono.empty());

    asDirector().patch().uri("/api/v1/materias/MAT001/estado")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {"estado":"INACTIVO"}
      """)
      .exchange()
      .expectStatus().isNoContent();
  }
}

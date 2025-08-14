package pe.edu.perumar.perumar_backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.perumar.perumar_backend.model.Ciclo;
import pe.edu.perumar.perumar_backend.repository.CicloRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CicloControllerIT {

  @Autowired WebTestClient webTestClient;
  @MockBean CicloRepository repo;

  private WebTestClient asDirector() {
    return webTestClient.mutateWith(
      mockJwt().authorities(new SimpleGrantedAuthority("ROLE_DIRECTOR"))
    );
  }

  private Ciclo sample(String id) {
    Ciclo c = new Ciclo();
    // Campos según tu modelo/servicio y response
    c.setId(id);
    c.setCodigoCarrera("CAR001");
    c.setNombreCiclo("2025-01-CAR001");
    c.setFechaInicio(LocalDate.of(2025, 3, 1));
    c.setFechaFin(LocalDate.of(2025, 7, 31));
    c.setMaterias(List.of("MAT001","MAT002"));
    c.setPromocion("2025-1");
    c.setUbicacion(Map.of("departamento","LIMA"));
    c.setEstado("ACTIVO");
    c.setCreatedAt(Instant.now());
    c.setUpdatedAt(Instant.now());
    return c;
  }

  @Test
  void post_crear_201() {
    // Service: valida fechas, check overlaps, calcula correlativo, crea Ciclo.nuevo(), save
    when(repo.findOverlaps(eq("CAR001"), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Flux.empty());
    when(repo.nextCorrelativo(eq("CAR001"), eq(2025)))
        .thenReturn(Mono.just(1));
    when(repo.save(any(Ciclo.class)))
        .thenAnswer(inv -> Mono.just(inv.getArgument(0, Ciclo.class)));

    asDirector().post().uri("/api/v1/ciclos")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {
          "codigoCarrera":"CAR001",
          "fechaInicio":"2025-03-01",
          "fechaFin":"2025-07-31",
          "materias":["MAT001","MAT002"],
          "promocion":"2025-1",
          "ubicacion":{"departamento":"LIMA"}
        }
      """)
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.id").exists()
      .jsonPath("$.codigoCarrera").isEqualTo("CAR001")
      .jsonPath("$.estado").isEqualTo("ACTIVO");
  }

  @Test
  void get_listar_por_estado_200() {
    when(repo.findByEstado(eq("ACTIVO"))).thenReturn(Flux.just(sample("ID001")));

    asDirector().get().uri(uriBuilder ->
        uriBuilder.path("/api/v1/ciclos").queryParam("estado", "ACTIVO").build())
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$[0].id").isEqualTo("ID001")
      .jsonPath("$[0].codigoCarrera").isEqualTo("CAR001")
      .jsonPath("$[0].estado").isEqualTo("ACTIVO");
  }

  @Test
  void put_actualizar_200() {
    var actual = sample("ID001");
    when(repo.findById(eq("ID001"))).thenReturn(Mono.just(actual));
    when(repo.update(any(Ciclo.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0, Ciclo.class)));

    asDirector().put().uri("/api/v1/ciclos/ID001")
      .header("X-Tiene-Matriculas", "false")
      .header("X-Es-Director", "true")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {
          "codigoCarrera":"CAR001",
          "fechaInicio":"2025-03-01",
          "fechaFin":"2025-07-31",
          "materias":["MAT001","MAT002"],
          "promocion":"2025-1",
          "ubicacion":{"departamento":"LIMA"}
        }
      """)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.id").isEqualTo("ID001")
      .jsonPath("$.nombreCiclo").isEqualTo("2025-01-CAR001");
  }

  @Test
  void patch_cambiarEstado_204() {
    var actual = sample("ID001");
    when(repo.findById(eq("ID001"))).thenReturn(Mono.just(actual));
    when(repo.update(any(Ciclo.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0, Ciclo.class)));

    asDirector().patch().uri("/api/v1/ciclos/ID001/estado?estado=INACTIVO")
      .exchange()
      .expectStatus().isNoContent();
  }
}

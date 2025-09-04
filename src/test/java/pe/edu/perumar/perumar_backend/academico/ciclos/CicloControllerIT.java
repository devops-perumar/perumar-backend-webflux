package pe.edu.perumar.perumar_backend.academico.ciclos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.perumar.perumar_backend.acl.AccessControlService;
import pe.edu.perumar.perumar_backend.BaseIT;
import pe.edu.perumar.perumar_backend.academico.carreras.repository.CarreraRepository;
import pe.edu.perumar.perumar_backend.academico.ciclos.model.Ciclo;
import pe.edu.perumar.perumar_backend.academico.ciclos.repository.CicloRepository;
import pe.edu.perumar.perumar_backend.academico.materias.repository.MateriaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class CicloControllerIT extends BaseIT {

  @Autowired WebTestClient webTestClient;

  @MockitoBean AccessControlService accessControlService;
  @MockitoBean CicloRepository cicloRepository;
  @MockitoBean CarreraRepository carreraRepository;
  @MockitoBean MateriaRepository materiaRepository;

  Ciclo c1, c2;

  @BeforeEach
  void setup() {
    // Permitir todos los accesos
    when(accessControlService.requireAccess(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Mono.empty());

    c1 = new Ciclo();
    c1.setId(UUID.randomUUID().toString());
    c1.setCodigoCarrera("CAR001");
    c1.setNombreCiclo("2025-01-CAR001");
    c1.setFechaInicio(LocalDate.parse("2025-03-01"));
    c1.setFechaFin(LocalDate.parse("2025-06-30"));
    c1.setMaterias(List.of("MAT001"));
    c1.setPromocion("2025-I");
    c1.setEstado("ACTIVO");
    c1.setCreatedAt(Instant.parse("2025-01-01T00:00:00Z"));
    c1.setUpdatedAt(Instant.parse("2025-01-01T00:00:00Z"));

    c2 = new Ciclo();
    c2.setId(UUID.randomUUID().toString());
    c2.setCodigoCarrera("CAR002");
    c2.setNombreCiclo("2025-02-CAR002");
    c2.setFechaInicio(LocalDate.parse("2025-08-01"));
    c2.setFechaFin(LocalDate.parse("2025-12-15"));
    c2.setMaterias(List.of("MAT001","MAT002"));
    c2.setPromocion("2025-II");
    c2.setEstado("ACTIVO");
    c2.setCreatedAt(Instant.parse("2025-02-01T00:00:00Z"));
    c2.setUpdatedAt(Instant.parse("2025-02-01T00:00:00Z"));

    when(cicloRepository.findByEstado("ACTIVO")).thenReturn(Flux.just(c1, c2));
    when(cicloRepository.findById(eq(c1.getId()))).thenReturn(Mono.just(c1));
    when(cicloRepository.update(any(Ciclo.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
  }

  @Test
  void listar_porEstado_activo_ok() {
    webTestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/ciclos").queryParam("estado", "ACTIVO").build())
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$[0].id").isEqualTo(c1.getId())
        .jsonPath("$[0].nombreCiclo").isEqualTo("2025-01-CAR001")
        .jsonPath("$[0].codigoCarrera").isEqualTo("CAR001")
        .jsonPath("$[0].estado").isEqualTo("ACTIVO")
        .jsonPath("$[1].id").isEqualTo(c2.getId())
        .jsonPath("$[1].nombreCiclo").isEqualTo("2025-02-CAR002")
        .jsonPath("$[1].codigoCarrera").isEqualTo("CAR002")
        .jsonPath("$[1].estado").isEqualTo("ACTIVO");
  }

  @Test
  void cambiarEstado_inactivo_204() {
    String body = """
      {"estado":"INACTIVO"}
    """;

    webTestClient.patch()
        .uri("/api/v1/ciclos/{id}/estado", c1.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange()
        .expectStatus().isNoContent();

    verify(cicloRepository).update(argThat(c ->
        c.getId().equals(c1.getId()) && "INACTIVO".equals(c.getEstado())
    ));
  }
}

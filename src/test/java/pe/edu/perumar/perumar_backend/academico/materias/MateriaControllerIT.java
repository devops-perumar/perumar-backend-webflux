package pe.edu.perumar.perumar_backend.academico.materias;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.perumar.perumar_backend.acl.AccessControlService;
import pe.edu.perumar.perumar_backend.BaseIT;
import pe.edu.perumar.perumar_backend.academico.carreras.repository.CarreraRepository;
import pe.edu.perumar.perumar_backend.academico.ciclos.repository.CicloRepository;
import pe.edu.perumar.perumar_backend.academico.materias.model.Materia;
import pe.edu.perumar.perumar_backend.academico.materias.repository.MateriaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class MateriaControllerIT extends BaseIT {

  @Autowired
  WebTestClient webTestClient;

  @MockitoBean
  AccessControlService accessControlService;

  @MockitoBean
  MateriaRepository materiaRepository;

  @MockitoBean
  CarreraRepository carreraRepository;

  @MockitoBean
  CicloRepository cicloRepository;

  Materia m1, m2;

  @BeforeEach
  void setup() {
    doNothing().when(accessControlService)
               .requireAccess(anyString(), anyString(), anyString(), anyString());

    m1 = new Materia();
    m1.setCodigo("MAT001");
    m1.setNombre("Navegación I");
    m1.setEstado("ACTIVO");
    m1.setCreatedAt(Instant.parse("2025-01-01T00:00:00Z"));
    m1.setUpdatedAt(Instant.parse("2025-01-01T00:00:00Z"));

    m2 = new Materia();
    m2.setCodigo("MAT002");
    m2.setNombre("Cartografía");
    m2.setEstado("ACTIVO");
    m2.setCreatedAt(Instant.parse("2025-02-01T00:00:00Z"));
    m2.setUpdatedAt(Instant.parse("2025-02-01T00:00:00Z"));

    when(materiaRepository.findByEstado("ACTIVO")).thenReturn(Flux.just(m1, m2));
    when(materiaRepository.updateEstado(eq("MAT001"), eq("INACTIVO"))).thenReturn(Mono.empty());
  }

  @Test
  void listar_porEstado_activo_ok() {
    webTestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/materias").queryParam("estado", "ACTIVO").build())
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$[0].codigo").isEqualTo("MAT001")
        .jsonPath("$[1].codigo").isEqualTo("MAT002");
  }

  @Test
  void cambiarEstado_inactivo_204() {
    String body = """
      {"estado":"INACTIVO"}
    """;

    webTestClient.patch()
        .uri("/api/v1/materias/{codigo}/estado", "MAT001")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange()
        .expectStatus().isNoContent();

    verify(materiaRepository).updateEstado("MAT001", "INACTIVO");
  }
}

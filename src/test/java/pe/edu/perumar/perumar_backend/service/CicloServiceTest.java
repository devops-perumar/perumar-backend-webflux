package pe.edu.perumar.perumar_backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.perumar.perumar_backend.dto.CicloRequest;
import pe.edu.perumar.perumar_backend.model.Ciclo;
import pe.edu.perumar.perumar_backend.repository.CicloRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CicloServiceTest {

  @Mock
  CicloRepository repo;

  @InjectMocks
  CicloService svc;

  @Test
  void crea_ok_autogenera_nombre_y_valida_solapamiento() {
    when(repo.findOverlaps(anyString(), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Flux.empty()); // <- Flux<Ciclo> vacío

    when(repo.nextCorrelativo(anyString(), anyInt()))
        .thenReturn(Mono.just(1));

    when(repo.save(any(Ciclo.class)))
        .thenAnswer(inv -> Mono.just(inv.getArgument(0, Ciclo.class)));

    var req = new CicloRequest(
        "CAR001",
        LocalDate.of(2025, 9, 1),
        LocalDate.of(2025, 9, 30),
        null, null, null, null
    );

    StepVerifier.create(svc.crear(req))
        .expectNextMatches(resp -> resp.nombreCiclo().equals("2025-01-CAR001"))
        .verifyComplete();
  }

  @Test
  void crea_falla_por_superposicion() {
    // Devolver un Flux<Ciclo> (mock o instancia real), NO Flux<Object>
    when(repo.findOverlaps(anyString(), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Flux.just(mock(Ciclo.class)));

    var req = new CicloRequest(
        "CAR001",
        LocalDate.of(2025, 9, 1),
        LocalDate.of(2025, 9, 30),
        null, null, null, null
    );

    StepVerifier.create(svc.crear(req))
        .expectErrorMatches(ex -> ex instanceof IllegalStateException
            && ex.getMessage().contains("superpone"))
        .verify();
  }
}

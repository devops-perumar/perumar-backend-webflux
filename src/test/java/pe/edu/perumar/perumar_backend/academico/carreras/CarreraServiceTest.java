package pe.edu.perumar.perumar_backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.edu.perumar.perumar_backend.dto.CarreraEstadoRequest;
import pe.edu.perumar.perumar_backend.dto.CarreraRequest;
import pe.edu.perumar.perumar_backend.dto.CarreraUpdateRequest;
import pe.edu.perumar.perumar_backend.model.Carrera;
import pe.edu.perumar.perumar_backend.model.Materia;
import pe.edu.perumar.perumar_backend.model.ModalidadCarrera;
import pe.edu.perumar.perumar_backend.repository.CarreraRepository;
import pe.edu.perumar.perumar_backend.repository.MateriaRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CarreraServiceTest {

  @Mock
  CarreraRepository carreraRepo;

  @Mock
  MateriaRepository materiaRepo;

  @InjectMocks
  CarreraService service;

  CarreraRequest reqCreate;

  @BeforeEach
  void setUp() {
    reqCreate = new CarreraRequest();
    reqCreate.setCodigo("CAR001");
    reqCreate.setNombre("Patrón Embarcaciones Menores");
    reqCreate.setDescripcion("Formación básica");
    reqCreate.setModalidad(ModalidadCarrera.SIN_EXPERIENCIA); // enum
    reqCreate.setMaterias(List.of("MAT001", "MAT002"));
  }

  private void mockMateria(String cod) {
    Materia m = new Materia();
    m.setCodigo(cod);
    m.setNombre("X");
    m.setEstado("ACTIVO");
    when(materiaRepo.findByCodigo(cod)).thenReturn(Mono.just(m));
  }

  @Test
  void crear_ok_inicializaEstadoYFechas_yValidaMaterias() {
    when(carreraRepo.findByCodigo("CAR001")).thenReturn(Mono.empty());
    // materias válidas
    mockMateria("MAT001");
    mockMateria("MAT002");
    // devuelve el mismo objeto que se guarda
    when(carreraRepo.save(any(Carrera.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    StepVerifier.create(service.crear(reqCreate))
      .assertNext(c -> {
        assert "CAR001".equals(c.getCodigo());
        assert "Patrón Embarcaciones Menores".equals(c.getNombre());
        assert "Formación básica".equals(c.getDescripcion());
        assert c.getModalidad() == ModalidadCarrera.SIN_EXPERIENCIA;
        assert c.getMaterias().equals(List.of("MAT001","MAT002"));
        assert "ACTIVO".equals(c.getEstado()); // inicializado en mapper/service
        assert c.getCreatedAt() != null;
        assert c.getUpdatedAt() != null;
        assert !c.getCreatedAt().isAfter(Instant.now());
      })
      .verifyComplete();

    verify(carreraRepo).findByCodigo("CAR001");
    verify(materiaRepo).findByCodigo("MAT001");
    verify(materiaRepo).findByCodigo("MAT002");
    verify(carreraRepo).save(any(Carrera.class));
  }

  @Test
  void crear_duplicado_lanza409() {
    when(carreraRepo.findByCodigo("CAR001")).thenReturn(Mono.just(new Carrera()));

    StepVerifier.create(service.crear(reqCreate))
      .expectError(CarreraService.DuplicateKeyException.class)
      .verify();

    verify(carreraRepo).findByCodigo("CAR001");
    verify(carreraRepo, never()).save(any());
  }

  @Test
  void crear_materiaInexistente_lanza400() {
    when(carreraRepo.findByCodigo("CAR001")).thenReturn(Mono.empty());
    mockMateria("MAT001");
    when(materiaRepo.findByCodigo("MAT002")).thenReturn(Mono.empty()); // no existe

    StepVerifier.create(service.crear(reqCreate))
      .expectError(IllegalArgumentException.class)
      .verify();

    verify(carreraRepo).findByCodigo("CAR001");
    verify(materiaRepo).findByCodigo("MAT001");
    verify(materiaRepo).findByCodigo("MAT002");
    verify(carreraRepo, never()).save(any());
  }

  @Test
  void actualizar_ok_modificaCampos_yActualizaUpdatedAt() {
    // existente
    Carrera actual = new Carrera();
    actual.setCodigo("CAR001");
    actual.setNombre("Old");
    actual.setDescripcion("Old");
    actual.setModalidad(ModalidadCarrera.SIN_EXPERIENCIA);
    actual.setMaterias(List.of("MAT001","MATX"));
    actual.setEstado("ACTIVO");
    actual.setCreatedAt(Instant.parse("2025-01-01T00:00:00Z"));
    actual.setUpdatedAt(Instant.parse("2025-01-01T00:00:00Z"));

    when(carreraRepo.findByCodigo("CAR001")).thenReturn(Mono.just(actual));

    // validación de nuevas materias
    mockMateria("MAT001");
    mockMateria("MAT002");

    when(carreraRepo.update(any(Carrera.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    CarreraUpdateRequest upd = new CarreraUpdateRequest();
    upd.setNombre("Nuevo nombre");
    upd.setDescripcion("Nueva desc");
    upd.setModalidad(ModalidadCarrera.CON_EXPERIENCIA);
    upd.setMaterias(List.of("MAT001","MAT002"));

    StepVerifier.create(service.actualizar("CAR001", upd))
      .assertNext(c -> {
        assert "Nuevo nombre".equals(c.getNombre());
        assert "Nueva desc".equals(c.getDescripcion());
        assert c.getModalidad() == ModalidadCarrera.CON_EXPERIENCIA;
        assert c.getMaterias().equals(List.of("MAT001","MAT002"));
        assert "ACTIVO".equals(c.getEstado()); // no cambia aquí
        assert c.getUpdatedAt().isAfter(Instant.parse("2025-01-01T00:00:00Z"));
      })
      .verifyComplete();

    verify(carreraRepo).findByCodigo("CAR001");
    verify(materiaRepo).findByCodigo("MAT001");
    verify(materiaRepo).findByCodigo("MAT002");
    verify(carreraRepo).update(any(Carrera.class));
  }

  @Test
  void cambiarEstado_ok_llamaRepoUpdateEstado() {
    when(carreraRepo.updateEstado(eq("CAR001"), eq("INACTIVO"))).thenReturn(Mono.empty());

    CarreraEstadoRequest er = new CarreraEstadoRequest();
    er.setEstado("INACTIVO");

    StepVerifier.create(service.cambiarEstado("CAR001", er))
      .verifyComplete();

    verify(carreraRepo).updateEstado("CAR001", "INACTIVO");
  }
}

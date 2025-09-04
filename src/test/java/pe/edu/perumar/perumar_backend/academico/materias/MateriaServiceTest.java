package pe.edu.perumar.perumar_backend.academico.materias;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaEstadoRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaRequest;
import pe.edu.perumar.perumar_backend.academico.materias.dto.MateriaUpdateRequest;
import pe.edu.perumar.perumar_backend.academico.materias.model.Materia;
import pe.edu.perumar.perumar_backend.academico.materias.repository.MateriaRepository;
import pe.edu.perumar.perumar_backend.academico.materias.service.MateriaService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class MateriaServiceTest {

  @Mock
  MateriaRepository repo;

  @InjectMocks
  MateriaService service;

  MateriaRequest reqCreate;

  @BeforeEach
  void setUp() {
    reqCreate = new MateriaRequest();
    reqCreate.setCodigo("MAT001");
    reqCreate.setNombre("Matemáticas I");
    reqCreate.setDescripcion("Base");
  }

  @Test
  void crear_ok_inicializaEstadoYFechas() {
    when(repo.findByCodigo("MAT001")).thenReturn(Mono.empty());
    // Devuelve el objeto que se guarda
    when(repo.save(any(Materia.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    StepVerifier.create(service.crear(reqCreate))
      .assertNext(m -> {
        // valores esperados
        assertEquals("MAT001", m.getCodigo());
        assertEquals("Matemáticas I", m.getNombre());
        assertEquals("Base", m.getDescripcion());
        assertEquals("ACTIVO", m.getEstado());
        assertNotNull(m.getCreatedAt());
        assertNotNull(m.getUpdatedAt());
        // createdAt y updatedAt muy cercanos
        assertFalse(m.getCreatedAt().isAfter(Instant.now()));
      })
      .verifyComplete();

    verify(repo).findByCodigo("MAT001");
    verify(repo).save(any(Materia.class));
  }

  @Test
  void crear_duplicado_lanza409() {
    when(repo.findByCodigo("MAT001")).thenReturn(Mono.just(new Materia()));

    StepVerifier.create(service.crear(reqCreate))
      .expectError(MateriaService.DuplicateKeyException.class)
      .verify();

    verify(repo).findByCodigo("MAT001");
    verify(repo, never()).save(any());
  }

  @Test
  void crear_codigoVacio_lanzaError() {
    // código vacío provoca error inmediato sin tocar repo
    reqCreate.setCodigo(" ");

    StepVerifier.create(service.crear(reqCreate))
        .expectError(IllegalArgumentException.class)
        .verify();

    verify(repo, never()).findByCodigo(any());
    verify(repo, never()).save(any());
  }

  @Test
  void actualizar_ok_modificaNombreYDescripcion_noCambiaEstado() {
    Materia actual = new Materia();
    actual.setCodigo("MAT001");
    actual.setNombre("Old");
    actual.setDescripcion("Old");
    actual.setEstado("ACTIVO");
    actual.setCreatedAt(Instant.parse("2025-01-01T00:00:00Z"));
    actual.setUpdatedAt(Instant.parse("2025-01-01T00:00:00Z"));

    when(repo.findByCodigo("MAT001")).thenReturn(Mono.just(actual));
    when(repo.update(any(Materia.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    MateriaUpdateRequest upd = new MateriaUpdateRequest();
    upd.setNombre("Nuevo nombre");
    upd.setDescripcion("Nueva desc");

    StepVerifier.create(service.actualizar("MAT001", upd))
      .assertNext(m -> {
        assertEquals("Nuevo nombre", m.getNombre());
        assertEquals("Nueva desc", m.getDescripcion());
        assertEquals("ACTIVO", m.getEstado()); // no cambia aquí
        assertTrue(m.getUpdatedAt().isAfter(Instant.parse("2025-01-01T00:00:00Z")));
      })
      .verifyComplete();

    verify(repo).findByCodigo("MAT001");
    verify(repo).update(any(Materia.class));
  }

  @Test
  void actualizar_noExiste_retornaVacio() {
    when(repo.findByCodigo("MAT001")).thenReturn(Mono.empty());

    StepVerifier.create(service.actualizar("MAT001", new MateriaUpdateRequest()))
        .verifyComplete();

    verify(repo).findByCodigo("MAT001");
    verify(repo, never()).update(any());
  }

  @Test
  void cambiarEstado_ok_llamaRepoUpdateEstado() {
    when(repo.updateEstado(eq("MAT001"), eq("INACTIVO"))).thenReturn(Mono.empty());
    MateriaEstadoRequest er = new MateriaEstadoRequest();
    er.setEstado("INACTIVO");

    StepVerifier.create(service.cambiarEstado("MAT001", er))
      .verifyComplete();

    verify(repo).updateEstado("MAT001", "INACTIVO");
  }

  @Test
  void cambiarEstado_error_propagado() {
    when(repo.updateEstado(eq("MAT001"), eq("INACTIVO")))
        .thenReturn(Mono.error(new RuntimeException("db error")));
    MateriaEstadoRequest er = new MateriaEstadoRequest();
    er.setEstado("INACTIVO");

    StepVerifier.create(service.cambiarEstado("MAT001", er))
        .expectErrorMessage("db error")
        .verify();

    verify(repo).updateEstado("MAT001", "INACTIVO");
  }
}

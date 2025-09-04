package pe.edu.perumar.perumar_backend;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import pe.edu.perumar.perumar_backend.academico.carreras.repository.CarreraRepository;
import pe.edu.perumar.perumar_backend.academico.materias.repository.MateriaRepository;

@SpringBootTest
@ActiveProfiles("test")
// TestConfig supplies an in-memory CicloRepository implementation.
@Import({PerumarBackendApplicationTests.RepoStubs.class, TestConfig.class, DynamoTestConfig.class})
class PerumarBackendApplicationTests {

  @Test
  void contextLoads() { }

  @TestConfiguration
  static class RepoStubs {
    @Bean MateriaRepository materiaRepository() { return Mockito.mock(MateriaRepository.class); }
    @Bean CarreraRepository carreraRepository() { return Mockito.mock(CarreraRepository.class); }
  }
}

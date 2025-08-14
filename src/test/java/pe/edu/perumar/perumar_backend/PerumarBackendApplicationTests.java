package pe.edu.perumar.perumar_backend;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import pe.edu.perumar.perumar_backend.repository.MateriaRepository;
import pe.edu.perumar.perumar_backend.repository.CarreraRepository;
import pe.edu.perumar.perumar_backend.repository.CicloRepository;

@SpringBootTest
@ActiveProfiles("test")
@Import(PerumarBackendApplicationTests.RepoStubs.class)
class PerumarBackendApplicationTests {

  @Test
  void contextLoads() { }

  @TestConfiguration
  static class RepoStubs {
    @Bean MateriaRepository materiaRepository() { return Mockito.mock(MateriaRepository.class); }
    @Bean CarreraRepository carreraRepository() { return Mockito.mock(CarreraRepository.class); }
    @Bean CicloRepository   cicloRepository()   { return Mockito.mock(CicloRepository.class);   }
  }
}

package pe.edu.perumar.perumar_backend.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import pe.edu.perumar.perumar_backend.academico.carreras.repository.CarreraRepository;
import pe.edu.perumar.perumar_backend.academico.ciclos.repository.CicloRepository;
import pe.edu.perumar.perumar_backend.academico.materias.repository.MateriaRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestConfiguration
@Profile("test")
public class TestConfig {

  // ---- Mocks Repos ----
  @Bean @Primary
  MateriaRepository materiaRepository() { return Mockito.mock(MateriaRepository.class); }

  @Bean @Primary
  CarreraRepository carreraRepository() { return Mockito.mock(CarreraRepository.class); }

  @Bean @Primary
  CicloRepository cicloRepository() { return Mockito.mock(CicloRepository.class); }

  // ---- Stubs AWS mínimos ----
  @Bean @Primary
  DynamoDbClient dynamoDbClient() { return Mockito.mock(DynamoDbClient.class); }

  @Bean @Primary
  S3AsyncClient s3AsyncClient() { return Mockito.mock(S3AsyncClient.class); }

  @Bean
  InitializingBean defaults(
      MateriaRepository materiaRepository,
      CarreraRepository carreraRepository,
      CicloRepository   cicloRepository
  ) {
    return () -> {
      // ===== MATERIAS =====
      when(materiaRepository.findByCodigo(anyString())).thenReturn(Mono.empty());
      when(materiaRepository.findByEstado("ACTIVO")).thenReturn(Flux.empty());
      when(materiaRepository.existsByCodigo(anyString())).thenReturn(Mono.just(false));
      when(materiaRepository.save(any()))
          .thenAnswer(inv -> Mono.justOrEmpty(inv.getArgument(0)));
      when(materiaRepository.update(any()))
          .thenAnswer(inv -> Mono.justOrEmpty(inv.getArgument(0)));
      doReturn(Mono.empty()).when(materiaRepository).updateEstado(anyString(), anyString());

      // ===== CARRERAS =====
      when(carreraRepository.findByCodigo(anyString())).thenReturn(Mono.empty());
      when(carreraRepository.save(any()))
          .thenAnswer(inv -> Mono.justOrEmpty(inv.getArgument(0)));
      when(carreraRepository.update(any()))
          .thenAnswer(inv -> Mono.justOrEmpty(inv.getArgument(0)));
      // Si tu repo define existsByCodigo/updateEstado, puedes habilitarlos:
      // when(carreraRepository.existsByCodigo(anyString())).thenReturn(Mono.just(false));
      // doReturn(Mono.empty()).when(carreraRepository).updateEstado(anyString(), anyString());

      // ===== CICLOS =====
      when(cicloRepository.findById(anyString())).thenReturn(Mono.empty());
      // (Si NO hay findAll() en la interfaz, no lo mocks)
      when(cicloRepository.save(any()))
          .thenAnswer(inv -> Mono.justOrEmpty(inv.getArgument(0)));
      when(cicloRepository.update(any()))
          .thenAnswer(inv -> Mono.justOrEmpty(inv.getArgument(0)));
      // Si tu interfaz define updateEstado(...) como Mono<Void>:
      // doReturn(Mono.empty()).when(cicloRepository).updateEstado(anyString(), anyString());
    };
  }
}

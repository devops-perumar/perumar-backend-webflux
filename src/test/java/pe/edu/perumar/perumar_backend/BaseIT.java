package pe.edu.perumar.perumar_backend;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.perumar.perumar_backend.config.TestConfig;
import pe.edu.perumar.perumar_backend.config.TestSecurityConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import({
  TestConfig.class,         // mocks de repos + stubs AWS
  TestSecurityConfig.class, // seguridad permitAll en test
  DynamoTestConfig.class    // clientes DynamoDB simulados
})
public abstract class BaseIT {

  @Autowired protected WebTestClient webTestClient;

  @BeforeEach
  void setupJwt() {
    webTestClient =
        webTestClient.mutateWith(
            mockJwt().jwt(jwt -> jwt.claim("cognito:groups", List.of("ROLE_TEST"))));
  }
}

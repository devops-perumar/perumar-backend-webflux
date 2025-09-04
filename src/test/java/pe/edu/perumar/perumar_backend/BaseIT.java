package pe.edu.perumar.perumar_backend;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

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
public abstract class BaseIT { }

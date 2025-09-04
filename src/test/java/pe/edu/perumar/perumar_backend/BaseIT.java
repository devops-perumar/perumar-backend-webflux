package pe.edu.perumar.perumar_backend;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import pe.edu.perumar.perumar_backend.config.TestProfileConfig;
import pe.edu.perumar.perumar_backend.config.TestSecurityConfig;
import pe.edu.perumar.perumar_backend.DynamoTestConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import({
  TestProfileConfig.class,   // mocks de repos + stubs AWS
  TestSecurityConfig.class,  // seguridad permitAll en test
  DynamoTestConfig.class     // mock de DynamoDbClient
})
public abstract class BaseIT { }

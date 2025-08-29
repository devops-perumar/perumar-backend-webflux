package pe.edu.perumar.perumar_backend;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import({
  pe.edu.perumar.perumar_backend.config.TestProfileConfig.class,   // mocks de repos + stubs AWS
  pe.edu.perumar.perumar_backend.config.TestSecurityConfig.class,  // seguridad permitAll en test
  pe.edu.perumar.perumar_backend.DynamoTestConfig.class            // mock de DynamoDbClient
})
public abstract class BaseIT { }

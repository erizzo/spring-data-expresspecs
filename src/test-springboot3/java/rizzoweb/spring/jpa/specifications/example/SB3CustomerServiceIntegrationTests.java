package expresspecs.example;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import expresspecs.SB3JpaTestConfig;


@DataJpaTest
@ContextConfiguration(classes = SB3JpaTestConfig.class)
public class SB3CustomerServiceIntegrationTests extends CustomersServiceIntegrationTests {
}

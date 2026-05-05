package expresspecs;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = SB3JpaTestConfig.class)
class SB3SpecificationIntegrationTests extends SpecificationIntegrationTests {
}

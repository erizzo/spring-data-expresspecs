package expresspecs;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for Testcontainers-backed specification integration tests.
 *
 * <p>Loads a full (non-web) Spring application context using {@link TCTestConfig}, which
 * enables Spring Boot auto-configuration so Hibernate and the datasource are configured
 * automatically from the properties supplied by the {@code @ServiceConnection} container
 * declared in each concrete subclass.
 *
 * <p>The full suite of specification tests defined in {@link SpecificationIntegrationTests}
 * runs against every database that has a concrete subclass of this class.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TCTestConfig.class)
@Testcontainers
abstract class AbstractContainerSpecificationTests extends SpecificationIntegrationTests {
}

package expresspecs;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

class PostgreSQLSpecificationTests extends AbstractContainerSpecificationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer db = new PostgreSQLContainer(ContainerImages.POSTGRESQL);
}

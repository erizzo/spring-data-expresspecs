package expresspecs;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;

class MariaDBSpecificationTests extends AbstractContainerSpecificationTests {

	@Container
	@ServiceConnection
	static MariaDBContainer<?> db = new MariaDBContainer<>("mariadb:11.4");
}

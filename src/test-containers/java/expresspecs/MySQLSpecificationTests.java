package expresspecs;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

class MySQLSpecificationTests extends AbstractContainerSpecificationTests {

	@Container
	@ServiceConnection
	static MySQLContainer db = new MySQLContainer("mysql:8.4");
}

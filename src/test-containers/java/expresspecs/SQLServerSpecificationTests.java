package expresspecs;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;

class SQLServerSpecificationTests extends AbstractContainerSpecificationTests {

	@Container
	@ServiceConnection
	static MSSQLServerContainer<?> db = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
			.acceptLicense();
}

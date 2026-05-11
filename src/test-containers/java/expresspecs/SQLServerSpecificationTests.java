package expresspecs;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mssqlserver.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;

class SQLServerSpecificationTests extends AbstractContainerSpecificationTests {

	@Container
	@ServiceConnection
	static MSSQLServerContainer db = createContainer();

	private static MSSQLServerContainer createContainer() {
		var container = new MSSQLServerContainer("mcr.microsoft.com/mssql/server:2022-latest");
		container.acceptLicense();
		return container;
	}
}

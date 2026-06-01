package expresspecs;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.oracle.OracleContainer;

class OracleSpecificationTests extends AbstractContainerSpecificationTests {

	// gvenzl/oracle-free:23-slim-faststart is the smallest image with the fastest startup.
	// Even so, expect long initialization time on first run while Docker pulls and initialises the image.
	@Container
	@ServiceConnection
	static OracleContainer db = new OracleContainer(ContainerImages.ORACLE);

	@Override
	@Disabled("isNotNullOrEmpty does not behave correctly on Oracle: Oracle coerces '' to NULL at storage "
			+ "time, so the column <> '' predicate binds as column <> NULL, which is UNKNOWN under SQL "
			+ "three-valued logic, causing every row to be excluded.")
	public void isNotNullOrEmpty() {}
}

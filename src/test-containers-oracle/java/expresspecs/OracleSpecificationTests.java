package expresspecs;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.oracle.OracleContainer;

class OracleSpecificationTests extends AbstractContainerSpecificationTests {

	// gvenzl/oracle-free:23-slim-faststart is the smallest image with the fastest startup.
	// Even so, expect ~60–90 s on first run while Docker pulls and initialises the image.
	@Container
	@ServiceConnection
	static OracleContainer db = new OracleContainer(ContainerImages.ORACLE);
}

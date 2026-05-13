package expresspecs;

import org.testcontainers.utility.DockerImageName;

/**
 * Central registry for Docker images used in Testcontainers tests.
 *
 * <p>Each image has a default that can be overridden at build time by setting
 * a corresponding JVM system property (e.g. via Maven):
 *
 * <pre>
 *   ./mvnw clean test -Psb4,tc -Dtc.image.postgresql=postgres:16-alpine
 * </pre>
 */
public final class ContainerImages {

	private ContainerImages() {
		// Utility class
	}

	/** Image for use in {@code PostgreSQLContainer}. */
	public static final DockerImageName POSTGRESQL  = resolve("postgresql",  "postgres:17-alpine", "postgres");
	
	/** Image for use in {@code MySQLContainer}. */
	public static final DockerImageName MYSQL       = resolve("mysql",       "mysql:8.4",          "mysql");
	
	/** Image for use in {@code MariaDBContainer}. */
	public static final DockerImageName MARIADB     = resolve("mariadb",     "mariadb:11.4",       "mariadb");
	
	/** Image for use in {@code MSSQLServerContainer}. */
	public static final DockerImageName MSSQLSERVER = resolve("mssqlserver", "mcr.microsoft.com/mssql/server:2022-latest", "mcr.microsoft.com/mssql/server");
	
	/** Image for use in {@code OracleContainer}. */
	public static final DockerImageName ORACLE      = resolve("oracle",      "gvenzl/oracle-free:23-slim-faststart",       "gvenzl/oracle-free");

	private static DockerImageName resolve(String key, String defaultImage, String compatibleWith) {
		String resolved = System.getProperty("tc.image." + key, defaultImage);
		return DockerImageName.parse(resolved).asCompatibleSubstituteFor(compatibleWith);
	}
}

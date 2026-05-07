package expresspecs;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import expresspecs.example.ComponentScanMarker;
import jakarta.persistence.EntityManager;
import rizzoweb.spring.jpa.EntityManagerWrapper;

/**
 * Spring configuration for Testcontainers-backed integration tests.
 *
 * <p>Uses {@link EnableAutoConfiguration} so Spring Boot configures the datasource and
 * Hibernate automatically. The datasource URL, username, and password are supplied at
 * runtime by the {@code @ServiceConnection} container declared in each concrete test class,
 * overriding the H2 URL in {@code application.yaml}.
 *
 * <p>Uses {@link EntityManager} directly (rather than {@code TestEntityManager}) because
 * {@code TestEntityManager} is only available in the {@code @DataJpaTest} slice.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = ComponentScanMarker.class)
@EntityScan(basePackageClasses = ComponentScanMarker.class)
@EnableJpaRepositories(basePackageClasses = ComponentScanMarker.class)
public class TCTestConfig {

	@Bean
	EntityManagerWrapper entityManager(EntityManager em) {
		return new EntityManagerWrapper() {

			@Override
			public <T> @NonNull T persistAndFlush(T entity) {
				em.persist(entity);
				em.flush();
				return entity;
			}

			@Override
			public void clear() {
				em.clear();
			}
		};
	}
}

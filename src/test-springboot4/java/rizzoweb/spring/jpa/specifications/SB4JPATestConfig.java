package rizzoweb.spring.jpa.specifications;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import rizzoweb.spring.jpa.EntityManagerWrapper;


@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = rizzoweb.spring.jpa.specifications.example.ComponentScanMarker.class)
@EntityScan(basePackageClasses = rizzoweb.spring.jpa.specifications.example.ComponentScanMarker.class)
@EnableJpaRepositories(basePackageClasses = rizzoweb.spring.jpa.specifications.example.ComponentScanMarker.class)
public class SB4JPATestConfig {

	@Bean
	EntityManagerWrapper entityManager(TestEntityManager entityManager) {
		return new EntityManagerWrapper() {

			@Override
			public <T> @NonNull T persistAndFlush(T entity) {
				if (entity == null) {
					return null;
				} else {
					return entityManager.persistAndFlush(entity);
				}
			}

			@Override
			public void clear() {
				entityManager.clear();
			}
		};
	}
}

package rizzoweb.spring.jpa.specifications;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = rizzoweb.spring.jpa.specifications.test.ComponentScanMarker.class)
@EntityScan(basePackageClasses = rizzoweb.spring.jpa.specifications.test.ComponentScanMarker.class)
@EnableJpaRepositories(basePackageClasses = rizzoweb.spring.jpa.specifications.test.ComponentScanMarker.class)
public class SB3JpaTestConfig {

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

package rizzoweb.spring.jpa.specifications;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EnableAutoConfiguration
@EntityScan(basePackageClasses = rizzoweb.spring.jpa.specifications.test.ComponentScanMarker.class)
@EnableJpaRepositories(basePackageClasses = rizzoweb.spring.jpa.specifications.test.ComponentScanMarker.class)
class TestJpaConfig {
}
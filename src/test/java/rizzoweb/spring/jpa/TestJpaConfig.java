package rizzoweb.spring.jpa;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EnableAutoConfiguration
@EntityScan(basePackageClasses = rizzoweb.spring.jpa.test.ComponentScanMarker.class)
@EnableJpaRepositories(basePackageClasses = rizzoweb.spring.jpa.test.ComponentScanMarker.class)
class TestJpaConfig {
}
package rizzoweb.spring.jpa.specifications.example;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import rizzoweb.spring.jpa.specifications.SB3JpaTestConfig;


@DataJpaTest
@ContextConfiguration(classes = SB3JpaTestConfig.class)
public class SB3CustomerServiceIntegrationTests extends CustomersServiceIntegrationTests {
}

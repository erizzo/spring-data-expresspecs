package rizzoweb.spring.jpa.specifications.example;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import rizzoweb.spring.jpa.specifications.SB4JPATestConfig;

@DataJpaTest
@ContextConfiguration(classes = SB4JPATestConfig.class)
public class SB4CustomersServiceIntegrationTests extends CustomersServiceIntegrationTests {
}

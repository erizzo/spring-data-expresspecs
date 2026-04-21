package rizzoweb.spring.jpa.specifications.test;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = rizzoweb.spring.jpa.specifications.TestJpaConfig.class)
public class CustomerServiceIntegrationTests extends BaseCustomerServiceIntegrationTests {
}

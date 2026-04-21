package rizzoweb.spring.jpa.specifications.test;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;


@DataJpaTest
@ContextConfiguration(classes = rizzoweb.spring.jpa.specifications.TestJpaConfig.class)
public class CustomerServiceIntegrationTests extends BaseCustomerServiceIntegrationTests {
}

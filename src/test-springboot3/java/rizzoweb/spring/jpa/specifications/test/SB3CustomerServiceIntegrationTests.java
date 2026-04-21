package rizzoweb.spring.jpa.specifications.test;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;


@DataJpaTest
@ContextConfiguration(classes = rizzoweb.spring.jpa.specifications.SB3JpaTestConfig.class)
public class SB3CustomerServiceIntegrationTests extends BaseCustomerServiceIntegrationTests {
}

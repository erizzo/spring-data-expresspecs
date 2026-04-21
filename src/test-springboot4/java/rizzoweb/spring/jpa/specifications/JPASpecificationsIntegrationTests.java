package rizzoweb.spring.jpa.specifications;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = TestJpaConfig.class)
class JPASpecificationsIntegrationTests extends BaseJPASpecificationsIntegrationTests {
}

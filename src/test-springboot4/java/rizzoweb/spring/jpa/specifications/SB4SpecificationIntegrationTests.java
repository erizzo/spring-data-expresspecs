package rizzoweb.spring.jpa.specifications;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import expresspecs.SpecificationIntegrationTests;

@DataJpaTest
@ContextConfiguration(classes = SB4JPATestConfig.class)
class SB4SpecificationIntegrationTests extends SpecificationIntegrationTests {
}

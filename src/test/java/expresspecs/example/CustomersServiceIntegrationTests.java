package expresspecs.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import rizzoweb.spring.jpa.BaseJPAIntegrationTest;

/**
 * Base class for {@link CustomersService} integration tests.
 */
@Transactional
public abstract class CustomersServiceIntegrationTests extends BaseJPAIntegrationTest {

	@Autowired
	protected CustomersService customersService;

	@Autowired
	@Getter
	protected CustomerRepository repo;


	@Test
	void findActiveCustomersByName() {
		Customer alice = Customer.builder().name("Alice").isActive(true).build();
		Customer bob = Customer.builder().name("Bob").isActive(true).build();
		Customer charlie = Customer.builder().name("Charlie").isActive(false).build();
		Customer aliceInactive = Customer.builder().name("Alice Inactive").isActive(false).build();

		persistAndFlush(alice);
		persistAndFlush(bob);
		persistAndFlush(charlie);
		persistAndFlush(aliceInactive);

		List<Customer> results = customersService.findActiveCustomersByName("ali");

		assertThat(results).containsExactly(alice);
	}
}

package rizzoweb.spring.jpa.specifications.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import rizzoweb.spring.jpa.BaseJPAIntegrationTest;

/**
 * Base class for {@link CustomerService} integration tests.
 */
@Transactional
public abstract class BaseCustomerServiceIntegrationTests extends BaseJPAIntegrationTest {

	@Autowired
	protected CustomerService customerService;

	@Autowired
	protected CustomerRepository repository;


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

		List<Customer> results = customerService.findActiveCustomersByName("ali");

		assertThat(results).containsExactly(alice);
	}
}

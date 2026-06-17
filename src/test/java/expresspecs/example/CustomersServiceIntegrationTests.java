package expresspecs.example;

import static expresspecs.SpecificationProjections.firstProjectedAs;
import static expresspecs.SpecificationProjections.oneProjectedAs;
import static expresspecs.SpecificationProjections.streamProjectedAs;
import static expresspecs.example.CustomerSpecifications.isActive;
import static expresspecs.example.CustomerSpecifications.nameIs;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import rizzoweb.spring.jpa.BaseJPAIntegrationTest;

/**
 * Base class for {@link CustomersService} integration tests.
 */
@Transactional
public abstract class CustomersServiceIntegrationTests extends BaseJPAIntegrationTest<Customer> {

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

	@Test
	void getActiveCustomers() {
		Customer alice = Customer.builder().name("Alice").isActive(true).build();
		Customer bob = Customer.builder().name("Bob").isActive(true).build();
		Customer charlie = Customer.builder().name("Charlie").isActive(false).build();

		persistAndFlush(alice);
		persistAndFlush(bob);
		persistAndFlush(charlie);

		Page<CustomerDTO> page = customersService.getActiveCustomers(PageRequest.ofSize(10));

		assertThat(page.getTotalElements()).isEqualTo(2);

		assertThat(page.getContent())
			.extracting(CustomerDTO::getName)
			.containsExactlyInAnyOrder(alice.getName(), bob.getName());

		assertThat(page.getContent())
			.extracting(CustomerDTO::getId)
			.containsExactlyInAnyOrder(alice.getId(), bob.getId());

		assertThat(page.getContent()).allSatisfy(dto -> assertThat(dto.isActive()).isTrue());
	}

	@Test
	void oneProjectedAs_returnsTheSingleMatch() {
		persistAndFlush(Customer.builder().name("Alice").isActive(true).build());
		persistAndFlush(Customer.builder().name("Bob").isActive(true).build());

		Optional<CustomerDTO> result = repo.findBy(nameIs("Alice"), oneProjectedAs(CustomerDTO.class));

		assertThat(result).isPresent();
		assertThat(result.get().getName()).isEqualTo("Alice");
	}

	@Test
	void oneProjectedAs_isEmptyWhenNothingMatches() {
		persistAndFlush(Customer.builder().name("Alice").isActive(true).build());

		Optional<CustomerDTO> result = repo.findBy(nameIs("Nobody"), oneProjectedAs(CustomerDTO.class));

		assertThat(result).isEmpty();
	}

	@Test
	void firstProjectedAs_returnsAMatchWhenSeveralExist() {
		persistAndFlush(Customer.builder().name("Alice").isActive(true).build());
		persistAndFlush(Customer.builder().name("Bob").isActive(true).build());
		persistAndFlush(Customer.builder().name("Charlie").isActive(false).build());

		Optional<CustomerDTO> result = repo.findBy(isActive(), firstProjectedAs(CustomerDTO.class));

		assertThat(result).isPresent();
		assertThat(result.get().getName()).isIn("Alice", "Bob");
		assertThat(result.get().isActive()).isTrue();
	}

	@Test
	void streamProjectedAs_streamsAllMatches() {
		persistAndFlush(Customer.builder().name("Alice").isActive(true).build());
		persistAndFlush(Customer.builder().name("Bob").isActive(true).build());
		persistAndFlush(Customer.builder().name("Charlie").isActive(false).build());

		try (Stream<CustomerDTO> stream = repo.findBy(isActive(), streamProjectedAs(CustomerDTO.class))) {
			assertThat(stream)
				.extracting(CustomerDTO::getName)
				.containsExactlyInAnyOrder("Alice", "Bob");
		}
	}
}

package rizzoweb.spring.jpa.specifications;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import rizzoweb.spring.jpa.DataIntegrationTest;
import rizzoweb.spring.jpa.specifications.example.Customer;
import rizzoweb.spring.jpa.specifications.example.CustomerSpecifications;
import rizzoweb.spring.jpa.specifications.example.Order;

@Transactional
public interface SpecificationExtensionsIntegrationTests extends DataIntegrationTest {

	@Test
	default void resultsAreDistinct() {
		var customer = Customer.builder().build();

		customer.addOrder(Order.builder()
							.datePlaced(LocalDate.now().minusDays(1))
							.build());
		customer.addOrder(Order.builder()
							.datePlaced(LocalDate.now().minusDays(2))
							.build());
		customer = persistAndFlush(customer);
		getEntityManager().clear();

		var spec = CustomerSpecifications.hasRecentOrder();
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(customer);
	}
}

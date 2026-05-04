package expresspecs;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import expresspecs.example.Customer;
import expresspecs.example.CustomerSpecifications;
import expresspecs.example.Order;
import rizzoweb.spring.jpa.DataIntegrationTest;

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

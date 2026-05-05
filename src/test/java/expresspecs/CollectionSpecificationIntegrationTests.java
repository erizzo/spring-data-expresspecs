package expresspecs;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import expresspecs.example.Customer;
import expresspecs.example.CustomerSpecifications;
import expresspecs.example.Order;

@Transactional
public interface CollectionSpecificationIntegrationTests extends BaseIntegrationTest {

	@Test
	default void containsMember() {
		Customer coyote = customer("Wile E. Coyote");
		Order acmeOrder = Order.builder().datePlaced(LocalDate.now()).build();
		coyote.addOrder(acmeOrder);
		persistAndFlush(coyote);

		Customer bunny = customer("Bugs Bunny");
		Order carrotOrder = Order.builder().datePlaced(LocalDate.now()).build();
		bunny.addOrder(carrotOrder);
		persistAndFlush(bunny);

		Specification<Customer> spec = CollectionSpecifications.containsMember(Customer.Fields.orders, acmeOrder);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void hasAnyOrders() {
		var withOrders = customer("with");
		withOrders.addOrder(Order.builder().datePlaced(LocalDate.now()).build());
		withOrders = persistAndFlush(withOrders);

		var withoutOrders = customer("without");
		withoutOrders = persistAndFlush(withoutOrders);

		Specification<Customer> spec = CustomerSpecifications.hasAnyOrders();
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(withOrders);
	}

	@Test
	default void hasAtLeastNOrders() {
		var threeOrders = customer("three");
		threeOrders.addOrder(Order.builder().datePlaced(LocalDate.now()).build());
		threeOrders.addOrder(Order.builder().datePlaced(LocalDate.now()).build());
		threeOrders.addOrder(Order.builder().datePlaced(LocalDate.now()).build());
		threeOrders = persistAndFlush(threeOrders);

		var oneOrder = customer("one");
		oneOrder.addOrder(Order.builder().datePlaced(LocalDate.now()).build());
		oneOrder = persistAndFlush(oneOrder);

		Specification<Customer> spec = CustomerSpecifications.hasAtLeastOrders(2);
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(threeOrders);
	}
}

package rizzoweb.spring.jpa.specifications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static rizzoweb.spring.jpa.specifications.RangeSpecifications.atLeast;
import static rizzoweb.spring.jpa.specifications.SpecificationExtensions.smartDistinct;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import rizzoweb.spring.jpa.specifications.example.Customer;
import rizzoweb.spring.jpa.specifications.example.Order;

@Transactional
public interface RangeSpecificationIntegrationTests extends SpecificationsIntegrationTest {

	@Test
	default void greaterThan_lessThan() {
		var bigSpender = Customer.builder()
						.creditLimit(10000)
						.build();
		bigSpender = persistAndFlush(bigSpender);

		var cheapskate =Customer.builder()
						.creditLimit(100)
						.build();
		cheapskate = persistAndFlush(cheapskate);

		// Test greaterThan()
		Specification<Customer> spec = RangeSpecifications.greaterThan(Customer.Fields.creditLimit, 1000);
		List<Customer> results = getRepo().findAll(spec);
		assertThat(results).containsExactly(bigSpender);

		// Test lessThan()
		spec = RangeSpecifications.lessThan(Customer.Fields.creditLimit, 1000);
		results = getRepo().findAll(spec);
		assertThat(results).containsExactly(cheapskate);
	}

	@Test
	default void between() {
		var bigSpender = Customer.builder()
						.creditLimit(10000)
						.build();
		bigSpender = persistAndFlush(bigSpender);

		var cheapskate =Customer.builder()
						.creditLimit(100)
						.build();
		cheapskate = persistAndFlush(cheapskate);

		Specification<Customer> spec = RangeSpecifications.between(Customer.Fields.creditLimit, 100, 10001);
		List<Customer> results = getRepo().findAll(spec);
		assertThat(results)
			.containsExactlyInAnyOrder(bigSpender, cheapskate);

		spec = RangeSpecifications.between(Customer.Fields.creditLimit, 0, 100);
		results = getRepo().findAll(spec);
		assertThat(results).isEmpty();
	}

	@Test
	default void between_NullBounds() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> RangeSpecifications.between(Customer.Fields.creditLimit, null, 1000));
		assertThatIllegalArgumentException()
			.isThrownBy(() -> RangeSpecifications.between(Customer.Fields.creditLimit, 0, null));
	}

	@Test
	default void atLeast_NestedProperty() {
		var newbie = customer("newb");

		newbie.addOrder(Order.builder()
							.datePlaced(LocalDate.now().minusDays(1))
							.build());
		newbie = persistAndFlush(newbie);

		var staleUser = customer("stale");
		staleUser.addOrder(Order.builder()
							.datePlaced(LocalDate.now().minusYears(1))
							.build());
		staleUser = persistAndFlush(staleUser);

		final var path = Customer.Fields.orders + '.' + Order.Fields.datePlaced;
		Specification<Customer> spec = atLeast(path, LocalDate.now().minusDays(30));
		spec = smartDistinct(spec);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(newbie);
	}
}

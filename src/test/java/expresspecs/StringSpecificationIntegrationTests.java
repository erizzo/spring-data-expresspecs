package expresspecs;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import expresspecs.example.Address;
import expresspecs.example.Customer;
import expresspecs.example.CustomerSpecifications;

@Transactional
public interface StringSpecificationIntegrationTests extends BaseIntegrationTest<Customer> {

	@Test
	default void contains() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		Specification<Customer> spec = StringSpecifications.contains(Customer.Fields.name, "Coy");
		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.nameContains("Coy");

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void contains_WithSpace() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Unknown"));

		Specification<Customer> spec = StringSpecifications.contains(Customer.Fields.name, " ");
		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.nameContains(" ");

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void contains_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
		Specification<Customer> spec = StringSpecifications.contains(path, "que");

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void nameContainsIgnoreCase() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		Specification<Customer> spec = CustomerSpecifications.nameContainsIgnoreCase("COYOTE");

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void containsIgnoreCase_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = Customer.Fields.address + '.' + Address.Fields.city;
		//Alternatively: var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
		Specification<Customer> spec = StringSpecifications.containsIgnoreCase(path, "QUE");

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void doesNotContain_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = Customer.Fields.address + '.' + Address.Fields.city;
		Specification<Customer> spec = StringSpecifications.doesNotContain(path, "que");

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(rabbit);
	}

	@Test
	default void doesNotContainIgnoreCase_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = Customer.Fields.address + '.' + Address.Fields.city;
		Specification<Customer> spec = StringSpecifications.doesNotContainIgnoreCase(path, "QUE");

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(rabbit);
	}

	@Test
	default void containsAny() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		List<String> searchTerms = List.of("Fudd", "Wile", "Bugs");
		Specification<Customer> spec = StringSpecifications.containsAny(Customer.Fields.name, searchTerms);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void containsAnyIgnoreCase() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		List<String> searchTerms = List.of("FUDD", "WILE", "BUGS");
		Specification<Customer> spec = StringSpecifications.containsAnyIgnoreCase(
				Customer.Fields.name, searchTerms);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void containsAny_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
		Specification<Customer> spec = StringSpecifications.containsAny(path, List.of("abc", "que", "xyz"));

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void containsAnyIgnoreCase_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
		Specification<Customer> spec = StringSpecifications.containsAnyIgnoreCase(path,
				List.of("ABC", "QUE", "XYZ"));

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void equalsIgnoreCase() {
		final String city = "Albuquerque";
		Customer coyote = customer("Wile E. Coyote", city(city));
		persistAndFlush(coyote.getAddress());
		persistAndFlush(coyote);

		Customer bunny = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(bunny.getAddress());
		persistAndFlush(bunny);

		// Match with different case
		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
		Specification<Customer> spec = StringSpecifications.equalsIgnoreCase(path, city.toLowerCase());

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}
}

package expresspecs;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import expresspecs.example.Address;
import expresspecs.example.Customer;
import expresspecs.example.PhoneNumber;

@Transactional
public interface BasicSpecificationIntegrationTests extends BaseIntegrationTest {

	@Test
	default void is() {
		final String wileE = "Wile E. Coyote";
		Customer coyote = customer(wileE);
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		Specification<Customer> spec = BasicSpecifications.is(Customer.Fields.name, wileE);
		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.nameIs(wileE);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void is_NestedProperty_NoMatches() {
		Address address = zipCode("33602");
		Customer customer = customer("c1", address);
		persistAndFlush(address);
		persistAndFlush(customer);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		Specification<Customer> spec = BasicSpecifications.is(path, "12345");

		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.hasZipCode("12345");

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).isEmpty();
	}

	@Test
	default void is_NestedProperty() {
		final String zipCode = "33602";
		Address address = zipCode(zipCode);
		Customer customer = customer("c1", address);
		persistAndFlush(address);
		customer = persistAndFlush(customer);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		Specification<Customer> spec = BasicSpecifications.is(path, zipCode);

		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.hasZipCode(zipCode);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(customer);
	}

	@Test
	default void is_NestedProperty_Embeddable() {
		final short areaCode = 813;
		Customer customer = Customer.builder()
								.phoneNumber(new PhoneNumber(813, 5551212, null))
								.build();
		customer = persistAndFlush(customer);

		persistAndFlush(Customer.builder().build());	// No phone number
		persistAndFlush(
				Customer.builder()
					.phoneNumber(new PhoneNumber(727, 1234567, null))	// Phone number but not matching area code
				.build());

		var path = PropertyPath.of(Customer.Fields.phoneNumber, PhoneNumber.Fields.areaCode);
		Specification<Customer> spec = BasicSpecifications.is(path, areaCode);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(customer);
	}

	@Test
	default void isNot() {
		final String wileE = "Wile E. Coyote";
		Customer coyote = customer(wileE);
		coyote = persistAndFlush(coyote);

		Customer roadRunner = customer("Road Runner");
		roadRunner = persistAndFlush(roadRunner);

		Specification<Customer> spec = BasicSpecifications.isNot(Customer.Fields.name, wileE);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(roadRunner);
	}

	@Test
	default void isNot_NestedProperty() {
		final String zipCode = "33602";
		Customer customer1 = customer("c1", zipCode(zipCode));
		persistAndFlush(customer1.getAddress());
		customer1 = persistAndFlush(customer1);

		Customer customer2 = customer("c2", zipCode("12345"));
		persistAndFlush(customer2.getAddress());
		customer2 = persistAndFlush(customer2);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		Specification<Customer> spec = BasicSpecifications.isNot(path, zipCode);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(customer2);
	}

	@Test
	default void isAny() {
		final String wileE = "Wile E. Coyote";
		Customer coyote = customer(wileE);
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		List<String> searchValues = List.of("Bugs Bunny", wileE, "Elmer Fudd");

		Specification<Customer> spec = BasicSpecifications.isAny(Customer.Fields.name, searchValues);

		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.nameIsOneOf(searchValues);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void isNotAny() {
		final String wileE = "Wile E. Coyote";
		final String roadRunner = "Road Runner";
		persistAndFlush(customer(wileE));
		Customer runner = persistAndFlush(customer(roadRunner));

		List<String> excludedValues = List.of("Bugs Bunny", wileE, "Elmer Fudd");

		Specification<Customer> spec = BasicSpecifications.isNotAny(Customer.Fields.name, excludedValues);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(runner);
	}

	@Test
	default void isAny_NestedProperty() {
		final String zipCode = "33602";
		Customer customer = customer("c1", zipCode(zipCode));
		persistAndFlush(customer.getAddress());
		customer = persistAndFlush(customer);

		List<String> zipCodes = List.of("12345", zipCode);
		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		Specification<Customer> spec = BasicSpecifications.isAny(path, zipCodes);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(customer);
	}

	@Test
	default void isNotAny_NestedProperty() {
		final String zipCode = "33602";
		Customer customer = customer("c1", zipCode(zipCode));
		persistAndFlush(customer.getAddress());
		persistAndFlush(customer);

		Customer other = customer("c2", zipCode("90210"));
		persistAndFlush(other.getAddress());
		persistAndFlush(other);

		List<String> zipCodes = List.of("12345", zipCode);
		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		Specification<Customer> spec = BasicSpecifications.isNotAny(path, zipCodes);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(other);
	}

	@Test
	default void isTrue_isFalse() {
		Customer coyote = customer("Wile E. Coyote");
		coyote.setActive(true);
		coyote = persistAndFlush(coyote);

		Customer inactive = customer("c2");
		inactive.setActive(false);
		inactive = persistAndFlush(inactive);

		Specification<Customer> spec = BasicSpecifications.isTrue(Customer.Fields.isActive);
		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.isActive();

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);

		spec = BasicSpecifications.isFalse(Customer.Fields.isActive);
		//spec = CustomerSpecifications.isNotActive();

		results = getRepo().findAll(spec);

		assertThat(results).containsExactly(inactive);
	}

	@Test
	default void isTrue_isFalse_NestedProperty() {
		Customer coyote = Customer.builder()
						.name("Wile E. Coyote")
						.address(Address.builder()
									.isPOBox(false)
									.build())
						.build();
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		Customer poBoxUser = Customer.builder()
						.name("Unknown")
						.address(Address.builder()
								.isPOBox(true)
								.build())
						.build();
		persistAndFlush(poBoxUser.getAddress());
		poBoxUser = persistAndFlush(poBoxUser);

		String path = "address.isPOBox";
		Specification<Customer> spec = BasicSpecifications.isTrue(path);

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(poBoxUser);

		spec = BasicSpecifications.isFalse(path);

		results = getRepo().findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void isNull_notNull() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		Customer unnamed = customer(null);		// No name
		unnamed = persistAndFlush(unnamed);

		// Test isNull()
		Specification<Customer> spec = BasicSpecifications.isNull(Customer.Fields.name);
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(unnamed);

		// Test notNull()
		spec = BasicSpecifications.notNull(Customer.Fields.name);
		results = getRepo().findAll(spec);
		assertThat(results).containsExactly(coyote);
	}

	@Test
	default void areEqual() {
		Customer customer1 = customer("Somewhere", city("Somewhere"));	 // Not realistic but useful for testing
		persistAndFlush(customer1.getAddress());
		customer1 = persistAndFlush(customer1);

		Customer customer2 = customer("Daffy Duck", city("Spitsville"));
		persistAndFlush(customer2.getAddress());
		customer2 = persistAndFlush(customer2);

		// Find entities where name is equal to city
		Specification<Customer> spec = BasicSpecifications.areEqual("name", "address.city");

		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(customer1);
	}
}

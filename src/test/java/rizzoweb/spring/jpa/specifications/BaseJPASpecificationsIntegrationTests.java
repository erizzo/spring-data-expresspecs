package rizzoweb.spring.jpa.specifications;

import static org.assertj.core.api.Assertions.assertThat;
import static rizzoweb.spring.jpa.specifications.JPASpecifications.atLeast;
import static rizzoweb.spring.jpa.specifications.JPASpecifications.smartDistinct;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import rizzoweb.spring.jpa.BaseJPAIntegrationTest;
import rizzoweb.spring.jpa.specifications.test.Address;
import rizzoweb.spring.jpa.specifications.test.Customer;
import rizzoweb.spring.jpa.specifications.test.CustomerRepository;
import rizzoweb.spring.jpa.specifications.test.CustomerSpecifications;
import rizzoweb.spring.jpa.specifications.test.Order;
import rizzoweb.spring.jpa.specifications.test.PhoneNumber;

@Transactional
public abstract class BaseJPASpecificationsIntegrationTests extends BaseJPAIntegrationTest {

	@Autowired
	protected CustomerRepository repo;


	private @NonNull Customer customer(String name) {
	    return Customer.builder()
                .name(name)
                .build();
	}

	private @NonNull Customer customer(String name, Address address) {
	    return Customer.builder()
	            .name(name)
	            .address(address)
	            .build();
	}

	private @NonNull Address city(String city) {
	    return Address.builder()
	            .city(city)
	            .build();
	}

	private @NonNull Address zipCode(String zipCode) {
	    return Address.builder()
	            .zipCode(zipCode)
	            .build();
	}

	@Test
	void contains() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		Specification<Customer> spec = JPASpecifications.contains(Customer.Fields.name, "Coy");
		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.nameContains("Coy");

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void contains_WithSpace() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Unknown"));

		Specification<Customer> spec = JPASpecifications.contains(Customer.Fields.name, " ");
		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.nameContains(" ");

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void contains_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
		Specification<Customer> spec = JPASpecifications.contains(path, "que");

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void nameContainsIgnoreCase() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		Specification<Customer> spec = CustomerSpecifications.nameContainsIgnoreCase("COYOTE");

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void containsIgnoreCase_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = Customer.Fields.address + '.' + Address.Fields.city;
		//Alternatively: var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
		Specification<Customer> spec = JPASpecifications.containsIgnoreCase(path, "QUE");

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void doesNotContain_NestedProperty() {
	    Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
	    persistAndFlush(coyote.getAddress());
	    coyote = persistAndFlush(coyote);

	    final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
	    persistAndFlush(rabbit.getAddress());
	    persistAndFlush(rabbit);

	    var path = Customer.Fields.address + '.' + Address.Fields.city;
	    Specification<Customer> spec = JPASpecifications.doesNotContain(path, "que");

	    List<Customer> results = repo.findAll(spec);

	    assertThat(results).containsExactly(rabbit);
	}

	@Test
	void doesNotContainIgnoreCase_NestedProperty() {
	    Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
	    persistAndFlush(coyote.getAddress());
	    coyote = persistAndFlush(coyote);

	    final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
	    persistAndFlush(rabbit.getAddress());
	    persistAndFlush(rabbit);

	    var path = Customer.Fields.address + '.' + Address.Fields.city;
	    Specification<Customer> spec = JPASpecifications.doesNotContainIgnoreCase(path, "QUE");

	    List<Customer> results = repo.findAll(spec);

	    assertThat(results).containsExactly(rabbit);
	}

	@Test
	void containsAny() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		List<String> searchTerms = List.of("Fudd", "Wile", "Bugs");
		Specification<Customer> spec = JPASpecifications.containsAny(Customer.Fields.name, searchTerms);

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void containsAnyIgnoreCase() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		List<String> searchTerms = List.of("FUDD", "WILE", "BUGS");
		Specification<Customer> spec = JPASpecifications.containsAnyIgnoreCase(
				Customer.Fields.name, searchTerms);

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void containsAny_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
		Specification<Customer> spec = JPASpecifications.containsAny(path, List.of("abc", "que", "xyz"));

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void containsAnyIgnoreCase_NestedProperty() {
		Customer coyote = customer("Wile E. Coyote", city("Albuquerque"));
		persistAndFlush(coyote.getAddress());
		coyote = persistAndFlush(coyote);

		final Customer rabbit = customer("Bugs Bunny", city("Atlanta"));
		persistAndFlush(rabbit.getAddress());
		persistAndFlush(rabbit);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
		Specification<Customer> spec = JPASpecifications.containsAnyIgnoreCase(path,
				List.of("ABC", "QUE", "XYZ"));

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void is() {
		final String wileE = "Wile E. Coyote";
		Customer coyote = customer(wileE);
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		Specification<Customer> spec = JPASpecifications.is(Customer.Fields.name, wileE);
		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.nameIs(wileE);

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void is_NestedProperty_NoMatches() {
		Address address = zipCode("33602");
		Customer customer = customer("c1", address);
		persistAndFlush(address);
		persistAndFlush(customer);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		Specification<Customer> spec = JPASpecifications.is(path, "12345");

		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.hasZipCode("12345");

		List<Customer> results = repo.findAll(spec);

		assertThat(results).isEmpty();
	}

	@Test
	void is_NestedProperty() {
		final String zipCode = "33602";
		Address address = zipCode(zipCode);
		Customer customer = customer("c1", address);
		persistAndFlush(address);
		customer = persistAndFlush(customer);

		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		Specification<Customer> spec = JPASpecifications.is(path, zipCode);

		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.hasZipCode(zipCode);

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(customer);
	}

	@Test
	void is_NestedProperty_Embeddable() {
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
		Specification<Customer> spec = JPASpecifications.is(path, areaCode);

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(customer);
	}

	@Test
	void isNot() {
        final String wileE = "Wile E. Coyote";
        Customer coyote = customer(wileE);
        coyote = persistAndFlush(coyote);

        Customer roadRunner = customer("Road Runner");
        roadRunner = persistAndFlush(roadRunner);

        Specification<Customer> spec = JPASpecifications.isNot(Customer.Fields.name, wileE);

        List<Customer> results = repo.findAll(spec);

        assertThat(results).containsExactly(roadRunner);
	}

	@Test
	void isNot_NestedProperty() {
        final String zipCode = "33602";
        Customer customer1 = customer("c1", zipCode(zipCode));
        persistAndFlush(customer1.getAddress());
        customer1 = persistAndFlush(customer1);

        Customer customer2 = customer("c2", zipCode("12345"));
        persistAndFlush(customer2.getAddress());
        customer2 = persistAndFlush(customer2);

        var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
        Specification<Customer> spec = JPASpecifications.isNot(path, zipCode);

        List<Customer> results = repo.findAll(spec);

        assertThat(results).containsExactly(customer2);
	}

	@Test
	void isAny() {
		final String wileE = "Wile E. Coyote";
		Customer coyote = customer(wileE);
		coyote = persistAndFlush(coyote);

		persistAndFlush(customer("Road Runner"));

		List<String> searchValues = List.of("Bugs Bunny", wileE, "Elmer Fudd");

		Specification<Customer> spec = JPASpecifications.isAny(Customer.Fields.name, searchValues);

		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.nameIsOneOf(searchValues);

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void isAny_NestedProperty() {
		final String zipCode = "33602";
		Customer customer = customer("c1", zipCode(zipCode));
		persistAndFlush(customer.getAddress());
		customer = persistAndFlush(customer);

		List<String> zipCodes = List.of("12345", zipCode);
		var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		Specification<Customer> spec = JPASpecifications.isAny(path, zipCodes);

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(customer);
	}

	@Test
	void isTrue_isFalse() {
		Customer coyote = customer("Wile E. Coyote");
		coyote.setActive(true);
		coyote = persistAndFlush(coyote);

		Customer inactive = customer("c2");
		inactive.setActive(false);
		inactive = persistAndFlush(inactive);

		Specification<Customer> spec = JPASpecifications.isTrue(Customer.Fields.isActive);
		// Equaivalent using the CustomerSpecifications domain-specific helper
		//Specification<Customer> spec = CustomerSpecifications.isActive();

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);

		spec = JPASpecifications.isFalse(Customer.Fields.isActive);
		//spec = CustomerSpecifications.isNotActive();

		results = repo.findAll(spec);

		assertThat(results).containsExactly(inactive);
	}

	@Test
	void isTrue_isFalse_NestedProperty() {
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
		Specification<Customer> spec = JPASpecifications.isTrue(path);

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(poBoxUser);

		spec = JPASpecifications.isFalse(path);

		results = repo.findAll(spec);

		assertThat(results).containsExactly(coyote);
	}

	@Test
	void isNull_notNull() {
		Customer coyote = customer("Wile E. Coyote");
		coyote = persistAndFlush(coyote);

		Customer unnamed = customer(null);    // No name
		unnamed = persistAndFlush(unnamed);

		// Test isNull()
		Specification<Customer> spec = JPASpecifications.isNull(Customer.Fields.name);
		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(unnamed);

		// Test notNull()
		spec = JPASpecifications.notNull(Customer.Fields.name);
		results = repo.findAll(spec);
		assertThat(results).containsExactly(coyote);
	}

	@Test
	void greaterThan_lessThan() {
		var bigSpender = Customer.builder()
						.creditLimit(10000)
						.build();
		bigSpender = persistAndFlush(bigSpender);

		var cheapskate =Customer.builder()
						.creditLimit(100)
						.build();
		cheapskate = persistAndFlush(cheapskate);

		// Test greaterThan()
		Specification<Customer> spec = JPASpecifications.greaterThan(Customer.Fields.creditLimit, 1000);
		List<Customer> results = repo.findAll(spec);
		assertThat(results).containsExactly(bigSpender);

		// Test lessThan()
		spec = JPASpecifications.lessThan(Customer.Fields.creditLimit, 1000);
		results = repo.findAll(spec);
		assertThat(results).containsExactly(cheapskate);
	}

	@Test
	void between() {
		var bigSpender = Customer.builder()
						.creditLimit(10000)
						.build();
		bigSpender = persistAndFlush(bigSpender);

		var cheapskate =Customer.builder()
						.creditLimit(100)
						.build();
		cheapskate = persistAndFlush(cheapskate);

		Specification<Customer> spec = JPASpecifications.between(Customer.Fields.creditLimit, 100, 10001);
		List<Customer> results = repo.findAll(spec);
		assertThat(results)
			.containsExactlyInAnyOrder(bigSpender, cheapskate);

		spec = JPASpecifications.between(Customer.Fields.creditLimit, 0, 100);
		results = repo.findAll(spec);
		assertThat(results).isEmpty();
	}

	@Test
	void atLeast_NestedProperty() {
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

		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(newbie);
	}

	@Test
	void areEqual() {
        Customer customer1 = customer("Somewhere", city("Somewhere"));   // Not realistic but useful for testing
        persistAndFlush(customer1.getAddress());
        customer1 = persistAndFlush(customer1);

        Customer customer2 = customer("Daffy Duck", city("Spitsville"));
        persistAndFlush(customer2.getAddress());
        customer2 = persistAndFlush(customer2);

        // Find entities where name is equal to city
        Specification<Customer> spec = JPASpecifications.areEqual("name", "address.city");

        List<Customer> results = repo.findAll(spec);

        assertThat(results).containsExactly(customer1);
	}

	@Test
	void onDate() {
		var newbie = Customer.builder()
						.createdTimestamp(OffsetDateTime.now().minusMonths(1))
						.build();
		newbie = persistAndFlush(newbie);

		var oldSchool = Customer.builder()
						.createdTimestamp(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"))
						.build();
		oldSchool = persistAndFlush(oldSchool);

		Specification<Customer> spec = JPASpecifications.onDate(Customer.Fields.createdTimestamp, LocalDate.parse("2007-12-03"));
		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(oldSchool);
	}

	@Test
	void resultsAreDistinct() {
		var customer = Customer.builder().build();

		customer.addOrder(Order.builder()
							.datePlaced(LocalDate.now().minusDays(1))
							.build());
		customer.addOrder(Order.builder()
							.datePlaced(LocalDate.now().minusDays(2))
							.build());
		customer = persistAndFlush(customer);
		entityManager.clear();

		var spec = CustomerSpecifications.hasRecentOrder();
		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(customer);
	}

}

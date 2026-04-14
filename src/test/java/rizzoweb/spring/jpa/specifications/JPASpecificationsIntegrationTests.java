package rizzoweb.spring.jpa.specifications;

import static org.assertj.core.api.Assertions.assertThat;
import static rizzoweb.spring.jpa.specifications.JPASpecifications.atLeast;
import static rizzoweb.spring.jpa.specifications.JPASpecifications.smartDistinct;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;

import rizzoweb.spring.jpa.specifications.test.Address;
import rizzoweb.spring.jpa.specifications.test.Customer;
import rizzoweb.spring.jpa.specifications.test.CustomerRepository;
import rizzoweb.spring.jpa.specifications.test.CustomerSpecifications;
import rizzoweb.spring.jpa.specifications.test.Order;
import rizzoweb.spring.jpa.specifications.test.PhoneNumber;

@DataJpaTest
@ContextConfiguration(classes = TestJpaConfig.class)
public class JPASpecificationsIntegrationTests {

	@Autowired
	private CustomerRepository repo;

	@Autowired
	private TestEntityManager entityManager;


	@Nested
	class ContainsVariants {
		@Test
		void contains() {
			Customer coyote = Customer.builder()
								.name("Wile E. Coyote")
								.build();
			coyote = entityManager.persistAndFlush(coyote);

			entityManager.persistAndFlush(Customer.builder().name("Road Runner").build());

			Specification<Customer> spec = JPASpecifications.contains(Customer.Fields.name, "Coy");
			// Equaivalent using the CustomerSpecifications domain-specific helper
			//Specification<Customer> spec = CustomerSpecifications.nameContains("Coy");

			List<Customer> results = repo.findAll(spec);

			assertThat(results).containsExactly(coyote);
		}

		@Test
		void contains_WithSpace() {
			Customer coyote = Customer.builder()
								.name("Wile E. Coyote")
								.build();
			coyote = entityManager.persistAndFlush(coyote);

			entityManager.persistAndFlush(Customer.builder().name("Unknown").build());

			Specification<Customer> spec = JPASpecifications.contains(Customer.Fields.name, " ");
			// Equaivalent using the CustomerSpecifications domain-specific helper
			//Specification<Customer> spec = CustomerSpecifications.nameContains(" ");

			List<Customer> results = repo.findAll(spec);

			assertThat(results).containsExactly(coyote);
		}

		@Test
		void contains_NestedProperty() {
			Customer coyote = Customer.builder()
								.name("Wile E. Coyote")
								.address(Address.builder().city("Albuquerque").build())
								.build();
			entityManager.persistAndFlush(coyote.getAddress());
			coyote = entityManager.persistAndFlush(coyote);

			final Customer bugs = Customer.builder()
									.name("Bugs Bunny")
									.address(Address.builder().city("Atlanta").build())
									.build();
			entityManager.persistAndFlush(bugs.getAddress());
			entityManager.persistAndFlush(bugs);

			var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
			Specification<Customer> spec = JPASpecifications.contains(path, "que");

			List<Customer> results = repo.findAll(spec);

			assertThat(results).containsExactly(coyote);
		}

		@Test
		void doesNotContain_NestedProperty() {
		    Customer coyote = Customer.builder()
                		            .name("Wile E. Coyote")
                		            .address(Address.builder().city("Albuquerque").build())
                		            .build();
		    entityManager.persistAndFlush(coyote.getAddress());
		    coyote = entityManager.persistAndFlush(coyote);

		    final Customer bugs = Customer.builder()
                		            .name("Bugs Bunny")
                		            .address(Address.builder().city("Atlanta").build())
                		            .build();
		    entityManager.persistAndFlush(bugs.getAddress());
		    entityManager.persistAndFlush(bugs);

		    var path = Customer.Fields.address + '.' + Address.Fields.city;
		    Specification<Customer> spec = JPASpecifications.doesNotContain(path, "que");

		    List<Customer> results = repo.findAll(spec);

		    assertThat(results).containsExactly(bugs);
		}

		@Test
		void containsAny() {
			Customer coyote = Customer.builder()
								.name("Wile E. Coyote")
								.build();
			coyote = entityManager.persistAndFlush(coyote);

			entityManager.persistAndFlush(Customer.builder().name("Road Runner").build());

			List<String> searchTerms = List.of("Fudd", "Wile", "Bugs");
			Specification<Customer> spec = JPASpecifications.containsAny(Customer.Fields.name, searchTerms);

			List<Customer> results = repo.findAll(spec);

			assertThat(results).containsExactly(coyote);
		}

		@Test
		void containsAny_NestedProperty() {
			Customer coyote = Customer.builder()
								.name("Wile E. Coyote")
								.address(Address.builder().city("Albuquerque").build())
								.build();
			entityManager.persistAndFlush(coyote.getAddress());
			coyote = entityManager.persistAndFlush(coyote);

			final Customer bugs = Customer.builder()
									.name("Bugs Bunny")
									.address(Address.builder().city("Atlanta").build())
									.build();
			entityManager.persistAndFlush(bugs.getAddress());
			entityManager.persistAndFlush(bugs);

			var path = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
			Specification<Customer> spec = JPASpecifications.containsAny(path, List.of("abc", "que", "xyz"));

			List<Customer> results = repo.findAll(spec);

			assertThat(results).containsExactly(coyote);
		}
	}


	@Nested
	class IsVariants {
		@Test
		void is() {
			final String wileE = "Wile E. Coyote";
			Customer coyote = Customer.builder()
								.name(wileE)
								.build();
			coyote = entityManager.persistAndFlush(coyote);

			entityManager.persistAndFlush(Customer.builder().name("Road Runner").build());

			Specification<Customer> spec = JPASpecifications.is(Customer.Fields.name, wileE);
			// Equaivalent using the CustomerSpecifications domain-specific helper
			//Specification<Customer> spec = CustomerSpecifications.nameIs(wileE);

			List<Customer> results = repo.findAll(spec);

			assertThat(results).containsExactly(coyote);
		}

		@Test
	    void is_NestedProperty_NoMatches() {
			Customer customer = Customer.builder()
	                            	.address(Address.builder()
											.zipCode("33602")
											.build())
	                            .build();
			entityManager.persistAndFlush(customer.getAddress());
			entityManager.persistAndFlush(customer);

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
			Customer customer = Customer.builder()
									.address(Address.builder()
											.zipCode(zipCode)
											.build())
									.build();
			entityManager.persistAndFlush(customer.getAddress());
			customer = entityManager.persistAndFlush(customer);

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
			customer = entityManager.persistAndFlush(customer);

			entityManager.persistAndFlush(Customer.builder().build());	// No phone number
			entityManager.persistAndFlush(
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
            Customer coyote = Customer.builder()
                                        .name(wileE)
                                        .build();
            coyote = entityManager.persistAndFlush(coyote);

            Customer roadRunner = Customer.builder().name("Road Runner").build();
            roadRunner = entityManager.persistAndFlush(roadRunner);

            Specification<Customer> spec = JPASpecifications.isNot(Customer.Fields.name, wileE);

            List<Customer> results = repo.findAll(spec);

            assertThat(results).containsExactly(roadRunner);
		}

        @Test
        void isNot_NestedProperty() {
            final String zipCode = "33602";
            Customer customer1 = Customer.builder()
                                    .address(Address.builder()
                                            .zipCode(zipCode)
                                            .build())
                                    .build();
            entityManager.persistAndFlush(customer1.getAddress());
            customer1 = entityManager.persistAndFlush(customer1);

            Customer customer2 = Customer.builder()
                                    .address(Address.builder()
                                            .zipCode("12345")
                                            .build())
                                    .build();
            entityManager.persistAndFlush(customer2.getAddress());
            customer2 = entityManager.persistAndFlush(customer2);

            var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
            Specification<Customer> spec = JPASpecifications.isNot(path, zipCode);

            List<Customer> results = repo.findAll(spec);

            assertThat(results).containsExactly(customer2);
        }

	}


	@Nested
	class IsAnyVariants {
		@Test
		void isAny() {
			final String wileE = "Wile E. Coyote";
			Customer coyote = Customer.builder()
					.name(wileE)
					.build();
			coyote = entityManager.persistAndFlush(coyote);

			entityManager.persistAndFlush(Customer.builder().name("Road Runner").build());

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
			Customer customer = Customer.builder()
								.address(Address.builder()
										.zipCode(zipCode)
										.build())
								.build();
			entityManager.persistAndFlush(customer.getAddress());
			customer = entityManager.persistAndFlush(customer);

			List<String> zipCodes = List.of("12345", zipCode);
			var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
			Specification<Customer> spec = JPASpecifications.isAny(path, zipCodes);

			List<Customer> results = repo.findAll(spec);

			assertThat(results).containsExactly(customer);
		}
	}


	@Nested
	class IsTrueIsFalse {
		@Test
		void isTrue_isFalse() {
			Customer coyote = Customer.builder()
								.name("Wile E. Coyote")
								.isActive(true)
								.build();
			coyote = entityManager.persistAndFlush(coyote);

			Customer inactive = Customer.builder()
								.name("Unknown")
								.isActive(false)
								.build();
			inactive = entityManager.persistAndFlush(inactive);

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
			entityManager.persist(coyote.getAddress());
			coyote = entityManager.persistAndFlush(coyote);

			Customer poBoxUser = Customer.builder()
								.name("Unknown")
								.address(Address.builder()
										.isPOBox(true)
										.build())
								.build();
			entityManager.persist(poBoxUser.getAddress());
			poBoxUser = entityManager.persistAndFlush(poBoxUser);

			String path = "address.isPOBox";
			Specification<Customer> spec = JPASpecifications.isTrue(path);

			List<Customer> results = repo.findAll(spec);

			assertThat(results).containsExactly(poBoxUser);

			spec = JPASpecifications.isFalse(path);

			results = repo.findAll(spec);

			assertThat(results).containsExactly(coyote);
		}
	}


	@Test
	void isNull_notNull() {
		Customer coyote = Customer.builder()
							.name("Wile E. Coyote")
							.isActive(true)
							.build();
		coyote = entityManager.persistAndFlush(coyote);

		Customer unnamed = Customer.builder()
							// no name is set
							.build();
		unnamed = entityManager.persistAndFlush(unnamed);

		// Test isNull()
		Specification<Customer> spec = JPASpecifications.isNull(Customer.Fields.name);
		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(unnamed);

		// Test notNull()
		spec = JPASpecifications.notNull(Customer.Fields.name);
		results = repo.findAll(spec);
		assertThat(results).containsExactly(coyote);
	}


	@Nested
	class ComparableMethods {
		@Test
		void greaterThan_lessThan() {
			var bigSpender = Customer.builder()
								.creditLimit(10000)
								.build();
			bigSpender = entityManager.persistAndFlush(bigSpender);

			var cheapskate =Customer.builder()
								.creditLimit(100)
								.build();
			cheapskate = entityManager.persistAndFlush(cheapskate);

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
			bigSpender = entityManager.persistAndFlush(bigSpender);

			var cheapskate =Customer.builder()
								.creditLimit(100)
								.build();
			cheapskate = entityManager.persistAndFlush(cheapskate);

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
			var newbie = Customer.builder().build();

			newbie.addOrder(Order.builder()
								.datePlaced(LocalDate.now().minusDays(1))
								.build());
			newbie = entityManager.persistAndFlush(newbie);

			var staleUser = Customer.builder().build();
			staleUser.addOrder(Order.builder()
								.datePlaced(LocalDate.now().minusYears(1))
								.build());
			staleUser = entityManager.persistAndFlush(staleUser);

		    final var path = Customer.Fields.orders + '.' + Order.Fields.datePlaced;
		    Specification<Customer> spec = atLeast(path, LocalDate.now().minusDays(30));
		    spec = smartDistinct(spec);

			List<Customer> results = repo.findAll(spec);

			assertThat(results).containsExactly(newbie);
		}
	}

    @Test
    void areEqual() {
        Customer customer1 = Customer.builder()
                                .name("Somewhere")  // Not realistic but useful for testing
                                .address(Address.builder()
                                        .city("Somewhere")
                                        .build())
                                .build();
        entityManager.persistAndFlush(customer1.getAddress());
        customer1 = entityManager.persistAndFlush(customer1);

        Customer customer2 = Customer.builder()
                                .name("Daffy Duck")
                                .address(Address.builder()
                                        .city("Spitsville")
                                        .build())
                                .build();
        entityManager.persistAndFlush(customer2.getAddress());
        customer2 = entityManager.persistAndFlush(customer2);

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
		newbie = entityManager.persistAndFlush(newbie);

		var oldSchool = Customer.builder()
							.createdTimestamp(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"))
							.build();
		oldSchool = entityManager.persistAndFlush(oldSchool);

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
		customer = entityManager.persistAndFlush(customer);
		entityManager.clear();

		var spec = CustomerSpecifications.hasRecentOrder();
		List<Customer> results = repo.findAll(spec);

		assertThat(results).containsExactly(customer);
	}

}

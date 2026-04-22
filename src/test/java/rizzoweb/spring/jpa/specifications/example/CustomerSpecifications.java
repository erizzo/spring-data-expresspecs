package rizzoweb.spring.jpa.specifications.example;


import static rizzoweb.spring.jpa.specifications.JPASpecifications.atLeast;
import static rizzoweb.spring.jpa.specifications.JPASpecifications.smartDistinct;

import java.time.LocalDate;
import java.util.Collection;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import rizzoweb.spring.jpa.specifications.JPASpecifications;
import rizzoweb.spring.jpa.specifications.PropertyPath;

public interface CustomerSpecifications {

	public static @NonNull Specification<Customer> nameIs(String name) {
		return JPASpecifications.is(Customer.Fields.name, name);
	}

	public static @NonNull Specification<Customer> nameIsOneOf(Collection<String> names) {
		return JPASpecifications.isAny(Customer.Fields.name, names);
	}

	public static @NonNull Specification<Customer> nameContains(String partialName) {
		return JPASpecifications.contains(Customer.Fields.name, partialName);
	}

	public static @NonNull Specification<Customer> nameContainsIgnoreCase(String partialName) {
		return JPASpecifications.containsIgnoreCase(Customer.Fields.name, partialName);
	}

	public static @NonNull Specification<Customer> isActive() {
		return JPASpecifications.isTrue(Customer.Fields.isActive);
	}

	public static @NonNull Specification<Customer> isNotActive() {
		return JPASpecifications.isFalse(Customer.Fields.isActive);
	}

	public static @NonNull Specification<Customer> isActive(boolean value) {
		return JPASpecifications.is(Customer.Fields.isActive, value);
	}

	public static @NonNull Specification<Customer> hasZipCode(String zipCode) {
		final var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		return JPASpecifications.is(path, zipCode);
	}

	public static @NonNull Specification<Customer> creditLimitOver(Integer minimum) {
		return JPASpecifications.greaterThan(Customer.Fields.creditLimit, minimum);
	}

	public static @NonNull Specification<Customer> hasRecentOrder() {
		final var path = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
		Specification<Customer> spec = atLeast(path, LocalDate.now().minusDays(30));
		return smartDistinct(spec);
	}
}

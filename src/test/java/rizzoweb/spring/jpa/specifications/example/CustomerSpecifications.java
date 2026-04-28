package rizzoweb.spring.jpa.specifications.example;


import static rizzoweb.spring.jpa.specifications.RangeSpecifications.atLeast;
import static rizzoweb.spring.jpa.specifications.JPASpecificationUtils.smartDistinct;

import java.time.LocalDate;
import java.util.Collection;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import rizzoweb.spring.jpa.specifications.BasicSpecifications;
import rizzoweb.spring.jpa.specifications.CollectionSpecifications;
import rizzoweb.spring.jpa.specifications.RangeSpecifications;
import rizzoweb.spring.jpa.specifications.StringSpecifications;
import rizzoweb.spring.jpa.specifications.PropertyPath;

public interface CustomerSpecifications {

	public static @NonNull Specification<Customer> nameIs(String name) {
		return BasicSpecifications.is(Customer.Fields.name, name);
	}

	public static @NonNull Specification<Customer> nameIsOneOf(Collection<String> names) {
		return BasicSpecifications.isAny(Customer.Fields.name, names);
	}

	public static @NonNull Specification<Customer> nameContains(String partialName) {
		return StringSpecifications.contains(Customer.Fields.name, partialName);
	}

	public static @NonNull Specification<Customer> nameContainsIgnoreCase(String partialName) {
		return StringSpecifications.containsIgnoreCase(Customer.Fields.name, partialName);
	}

	public static @NonNull Specification<Customer> isActive() {
		return BasicSpecifications.isTrue(Customer.Fields.isActive);
	}

	public static @NonNull Specification<Customer> isNotActive() {
		return BasicSpecifications.isFalse(Customer.Fields.isActive);
	}

	public static @NonNull Specification<Customer> isActive(boolean value) {
		return BasicSpecifications.is(Customer.Fields.isActive, value);
	}

	public static @NonNull Specification<Customer> hasZipCode(String zipCode) {
		final var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		return BasicSpecifications.is(path, zipCode);
	}

	public static @NonNull Specification<Customer> creditLimitOver(Integer minimum) {
		return RangeSpecifications.greaterThan(Customer.Fields.creditLimit, minimum);
	}

	public static @NonNull Specification<Customer> hasRecentOrder() {
		final var path = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
		Specification<Customer> spec = atLeast(path, LocalDate.now().minusDays(30));
		return smartDistinct(spec);
	}

	public static @NonNull Specification<Customer> hasAnyOrders() {
		return CollectionSpecifications.isNotEmpty(Customer.Fields.orders);
	}

	public static @NonNull Specification<Customer> hasAtLeastOrders(int minOrders) {
		return CollectionSpecifications.sizeAtLeast(Customer.Fields.orders, minOrders);
	}
}

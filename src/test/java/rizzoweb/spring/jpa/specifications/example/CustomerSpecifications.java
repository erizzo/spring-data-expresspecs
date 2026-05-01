package rizzoweb.spring.jpa.specifications.example;


import static rizzoweb.spring.jpa.specifications.BasicSpecifications.*;
import static rizzoweb.spring.jpa.specifications.CollectionSpecifications.isNotEmpty;
import static rizzoweb.spring.jpa.specifications.CollectionSpecifications.sizeAtLeast;
import static rizzoweb.spring.jpa.specifications.RangeSpecifications.atLeast;
import static rizzoweb.spring.jpa.specifications.RangeSpecifications.greaterThan;
import static rizzoweb.spring.jpa.specifications.SpecificationExtensions.smartDistinct;
import static rizzoweb.spring.jpa.specifications.StringSpecifications.contains;
import static rizzoweb.spring.jpa.specifications.StringSpecifications.containsIgnoreCase;

import java.time.LocalDate;
import java.util.Collection;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import rizzoweb.spring.jpa.specifications.PropertyPath;

public interface CustomerSpecifications {

	public static @NonNull Specification<Customer> nameIs(String name) {
		return is(Customer.Fields.name, name);
	}

	public static @NonNull Specification<Customer> nameIsOneOf(Collection<String> names) {
		return isAny(Customer.Fields.name, names);
	}

	public static @NonNull Specification<Customer> nameContains(String partialName) {
		return contains(Customer.Fields.name, partialName);
	}

	public static @NonNull Specification<Customer> nameContainsIgnoreCase(String partialName) {
		return containsIgnoreCase(Customer.Fields.name, partialName);
	}

	public static @NonNull Specification<Customer> isActive() {
		return isTrue(Customer.Fields.isActive);
	}

	public static @NonNull Specification<Customer> isNotActive() {
		return isFalse(Customer.Fields.isActive);
	}

	public static @NonNull Specification<Customer> isActive(boolean value) {
		return is(Customer.Fields.isActive, value);
	}

	public static @NonNull Specification<Customer> hasZipCode(String zipCode) {
		final var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		return is(path, zipCode);
	}

	public static @NonNull Specification<Customer> creditLimitOver(Integer minimum) {
		return greaterThan(Customer.Fields.creditLimit, minimum);
	}

	public static @NonNull Specification<Customer> hasRecentOrder() {
		final var path = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
		Specification<Customer> spec = atLeast(path, LocalDate.now().minusDays(30));
		return smartDistinct(spec);
	}

	public static @NonNull Specification<Customer> hasAnyOrders() {
		return isNotEmpty(Customer.Fields.orders);
	}

	public static @NonNull Specification<Customer> hasAtLeastOrders(int minOrders) {
		return sizeAtLeast(Customer.Fields.orders, minOrders);
	}
}

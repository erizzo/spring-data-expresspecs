package expresspecs.example;


import static expresspecs.BasicSpecifications.*;
import static expresspecs.CollectionSpecifications.isNotEmpty;
import static expresspecs.CollectionSpecifications.sizeAtLeast;
import static expresspecs.RangeSpecifications.atLeast;
import static expresspecs.RangeSpecifications.greaterThan;
import static expresspecs.SpecificationExtensions.smartDistinct;
import static expresspecs.StringSpecifications.contains;
import static expresspecs.StringSpecifications.containsIgnoreCase;

import java.time.LocalDate;
import java.util.Collection;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import expresspecs.PropertyPath;

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
		final var path = PropertyPath.of(Customer_.address, Address_.zipCode);
		return is(path, zipCode);
	}

	public static @NonNull Specification<Customer> creditLimitOver(Integer minimum) {
		return greaterThan(Customer.Fields.creditLimit, minimum);
	}

	public static @NonNull Specification<Customer> hasRecentOrder() {
		final var path = PropertyPath.of(Customer_.orders, Order_.datePlaced);
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

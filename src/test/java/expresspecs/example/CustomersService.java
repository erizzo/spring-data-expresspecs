package expresspecs.example;

import static expresspecs.BasicSpecifications.isAny;
import static expresspecs.BasicSpecifications.isTrue;
import static expresspecs.BasicSpecifications.where;
import static expresspecs.RangeSpecifications.atLeast;
import static expresspecs.StringSpecifications.containsIgnoreCase;
import static expresspecs.example.CustomerSpecifications.hasAnyOrders;
import static expresspecs.example.CustomerSpecifications.isActive;
import static expresspecs.example.CustomerSpecifications.nameContainsIgnoreCase;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import expresspecs.BasicSpecifications;
import lombok.RequiredArgsConstructor;

/**
 * Service for managing {@link Customer} entities.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomersService {

	private final CustomerRepository repository;

	/**
	 * Finds customers that are active and whose name contains the given partial name, ignoring case.
	 * Demonstrates using the {@link BasicSpecifications#where(Class, Specification)} method to anchor
	 * the type T during specification chaining.
	 */
	public List<Customer> findActiveCustomersByName(String partialName) {
		return repository.findAll(where(Customer.class,
									isTrue(Customer.Fields.isActive))
									.and(containsIgnoreCase(Customer.Fields.name, partialName)));
	}

	/**
	 * Finds customers whose address zip code is contained in the given list or who have a credit
	 * limit at least as large as the given minCredit.
	 * Demonstrates using individual specifications as local variables, chained together with {@link Specification#or(Specification)}.
	 */
	public List<Customer> findByZipCodeOrMinCredit(List<String> zipCodes, int minCredit) {
		Specification<Customer> zipMatches = isAny("address.zipCode", zipCodes);
		Specification<Customer> highCredit = atLeast("creditLimit", minCredit);
		return repository.findAll(zipMatches.or(highCredit));
	}

	/**
	 * Finds active customers with at least one order whose name contains the given partial name.
	 * Demonstrates domain-specific DSL methods as an alternative to local typed variables or {@code where()}.
	 */
	public List<Customer> findActiveCustomersWithOrdersByName(String partialName) {
		return repository.findAll(
							isActive()
							.and(nameContainsIgnoreCase(partialName))
							.and(hasAnyOrders()));
	}
}

package rizzoweb.spring.jpa.specifications;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.Getter;
import rizzoweb.spring.jpa.BaseJPAIntegrationTest;
import rizzoweb.spring.jpa.specifications.example.Address;
import rizzoweb.spring.jpa.specifications.example.Customer;
import rizzoweb.spring.jpa.specifications.example.CustomerRepository;

public abstract class SpecificationIntegrationTests extends BaseJPAIntegrationTest implements
		StringSpecificationIntegrationTests,
		BasicSpecificationIntegrationTests,
		RangeSpecificationIntegrationTests,
		DateTimeSpecificationIntegrationTests,
		CollectionSpecificationIntegrationTests,
		SpecificationUtilsIntegrationTests {

	@Autowired
	@Getter
	protected CustomerRepository repo;


	@Override
	public @NonNull Customer customer(String name) {
		return Customer.builder()
				.name(name)
				.build();
	}

	@Override
	public @NonNull Customer customer(String name, Address address) {
		return Customer.builder()
				.name(name)
				.address(address)
				.build();
	}

	@Override
	public @NonNull Address city(String city) {
		return Address.builder()
				.city(city)
				.build();
	}

	@Override
	public @NonNull Address zipCode(String zipCode) {
		return Address.builder()
				.zipCode(zipCode)
				.build();
	}
}

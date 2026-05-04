package expresspecs;

import org.jspecify.annotations.NonNull;

import expresspecs.example.Address;
import expresspecs.example.Customer;

public interface CustomerDataFactory {

	default @NonNull Customer customer(String name) {
		return Customer.builder()
				.name(name)
				.build();
	}

	default @NonNull Customer customer(String name, Address address) {
		return Customer.builder()
				.name(name)
				.address(address)
				.build();
	}

	default @NonNull Address city(String city) {
		return Address.builder()
				.city(city)
				.build();
	}

	default @NonNull Address zipCode(String zipCode) {
		return Address.builder()
				.zipCode(zipCode)
				.build();
	}
}

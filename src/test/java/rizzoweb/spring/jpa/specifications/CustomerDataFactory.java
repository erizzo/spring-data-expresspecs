package rizzoweb.spring.jpa.specifications;

import org.jspecify.annotations.NonNull;

import rizzoweb.spring.jpa.specifications.example.Address;
import rizzoweb.spring.jpa.specifications.example.Customer;

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

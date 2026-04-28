package rizzoweb.spring.jpa.specifications;

import org.jspecify.annotations.NonNull;

import rizzoweb.spring.jpa.EntityManagerWrapper;
import rizzoweb.spring.jpa.specifications.example.Address;
import rizzoweb.spring.jpa.specifications.example.Customer;
import rizzoweb.spring.jpa.specifications.example.CustomerRepository;

public interface SpecificationTestHelpers {
	CustomerRepository getRepo();
	EntityManagerWrapper getEntityManager();
	<T> T persistAndFlush(T entity);

	@NonNull Customer customer(String name);
	@NonNull Customer customer(String name, Address address);
	@NonNull Address city(String city);
	@NonNull Address zipCode(String zipCode);
}

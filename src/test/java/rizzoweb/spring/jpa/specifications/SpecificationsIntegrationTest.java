package rizzoweb.spring.jpa.specifications;

import rizzoweb.spring.jpa.EntityManagerWrapper;
import rizzoweb.spring.jpa.specifications.example.CustomerRepository;

public interface SpecificationsIntegrationTest extends CustomerDataFactory {
	CustomerRepository getRepo();
	EntityManagerWrapper getEntityManager();
	<T> T persistAndFlush(T entity);
}

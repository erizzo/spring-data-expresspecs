package rizzoweb.spring.jpa;

import rizzoweb.spring.jpa.specifications.example.CustomerRepository;

public interface DataIntegrationTest {

	CustomerRepository getRepo();

	EntityManagerWrapper getEntityManager();

	default <T> T persistAndFlush(T entity) {
		return getEntityManager().persistAndFlush(entity);
	}
}

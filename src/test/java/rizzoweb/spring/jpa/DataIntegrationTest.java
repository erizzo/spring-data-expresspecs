package rizzoweb.spring.jpa;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataIntegrationTest<E> {

	JpaSpecificationExecutor<E> getRepo();

	EntityManagerWrapper getEntityManager();

	default <T> T persistAndFlush(T entity) {
		return getEntityManager().persistAndFlush(entity);
	}
}

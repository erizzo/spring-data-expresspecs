package rizzoweb.spring.jpa;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;

public class BaseJPAIntegrationTest {

	@Autowired
	@Getter
	protected EntityManagerWrapper entityManager;


	public <T> T persistAndFlush(T entity) {
		return entityManager.persistAndFlush(entity);
	}

}
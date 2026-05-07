package rizzoweb.spring.jpa;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;

public abstract class BaseJPAIntegrationTest<E> implements DataIntegrationTest<E> {

	@Autowired
	@Getter
	protected EntityManagerWrapper entityManager;

}
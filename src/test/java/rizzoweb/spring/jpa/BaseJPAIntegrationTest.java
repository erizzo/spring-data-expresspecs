package rizzoweb.spring.jpa;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;

public abstract class BaseJPAIntegrationTest implements DataIntegrationTest {

	@Autowired
	@Getter
	protected EntityManagerWrapper entityManager;

}
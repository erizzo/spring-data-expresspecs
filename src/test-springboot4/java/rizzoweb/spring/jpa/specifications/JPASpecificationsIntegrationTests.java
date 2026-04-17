package rizzoweb.spring.jpa.specifications;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = TestJpaConfig.class)
class JPASpecificationsIntegrationTests extends BaseJPASpecificationsIntegrationTests {

	@Autowired
	private TestEntityManager entityManager;

	@Override
	protected <T> @NonNull T persistAndFlush(T entity) {
		return entityManager.persistAndFlush(entity);
	}

	@Override
	protected void clear() {
		entityManager.clear();
	}

}

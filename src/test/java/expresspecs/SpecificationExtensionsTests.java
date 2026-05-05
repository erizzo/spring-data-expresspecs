package expresspecs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class SpecificationExtensionsTests {

	@Mock private Root<Object> root;
	@Mock private CriteriaQuery<?> query;
	@Mock private CriteriaBuilder cb;

	@Nested
	class SafeOr {

		@Test
		void bothNull_ReturnsNull() {
			Specification<Object> result = SpecificationExtensions.safeOr(null, null);
			assertThat(result).isNull();
		}

		@Test
		void existingNull_ReturnsAdditional() {
			Specification<Object> additional = (r, q, cb) -> cb.conjunction();
			Specification<Object> result = SpecificationExtensions.safeOr(null, additional);
			assertThat(result).isSameAs(additional);
		}

		@Test
		void additionalNull_ReturnsExisting() {
			Specification<Object> existing = (r, q, cb) -> cb.conjunction();
			Specification<Object> result = SpecificationExtensions.safeOr(existing, null);
			assertThat(result).isSameAs(existing);
		}

		@Test
		void bothPresent_CombinesWithOr() {
			Predicate existingPredicate = mock(Predicate.class);
			Predicate additionalPredicate = mock(Predicate.class);
			Predicate combinedPredicate = mock(Predicate.class);

			Specification<Object> existing = (r, q, cb) -> existingPredicate;
			Specification<Object> additional = (r, q, cb) -> additionalPredicate;

			when(cb.or(existingPredicate, additionalPredicate)).thenReturn(combinedPredicate);

			Specification<Object> result = SpecificationExtensions.safeOr(existing, additional);

			assertThat(result).isNotNull();
			Predicate predicate = result.toPredicate(root, query, cb);
			assertThat(predicate).isSameAs(combinedPredicate);
		}
	}

	@Nested
	class SafeAnd {

		@Test
		void bothNull_ReturnsNull() {
			Specification<Object> result = SpecificationExtensions.safeAnd(null, null);
			assertThat(result).isNull();
		}

		@Test
		void existingNull_ReturnsAdditional() {
			Specification<Object> additional = (r, q, cb) -> cb.conjunction();
			Specification<Object> result = SpecificationExtensions.safeAnd(null, additional);
			assertThat(result).isSameAs(additional);
		}

		@Test
		void additionalNull_ReturnsExisting() {
			Specification<Object> existing = (r, q, cb) -> cb.conjunction();
			Specification<Object> result = SpecificationExtensions.safeAnd(existing, null);
			assertThat(result).isSameAs(existing);
		}

		@Test
		void bothPresent_CombinesWithAnd() {
			Predicate existingPredicate = mock(Predicate.class);
			Predicate additionalPredicate = mock(Predicate.class);
			Predicate combinedPredicate = mock(Predicate.class);

			Specification<Object> existing = (r, q, cb) -> existingPredicate;
			Specification<Object> additional = (r, q, cb) -> additionalPredicate;

			when(cb.and(existingPredicate, additionalPredicate)).thenReturn(combinedPredicate);

			Specification<Object> result = SpecificationExtensions.safeAnd(existing, additional);

			assertThat(result).isNotNull();
			Predicate predicate = result.toPredicate(root, query, cb);
			assertThat(predicate).isSameAs(combinedPredicate);
		}
	}
}

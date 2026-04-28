package rizzoweb.spring.jpa.specifications;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;

/**
 * Low-level utilities and structural helpers for JPA Specifications.
 */
@UtilityClass
public class JPASpecificationUtils {

	/**
	 * Wraps a {@link Specification} to ensure that results are distinct, but only
	 * when necessary.
	 * <p>
	 * This decorator performs two critical checks before applying a {@code DISTINCT} keyword:
	 * <ol>
	 * <li><b>Join Detection:</b> It only applies distinct logic if the query has
	 * actually performed a {@code JOIN}. This avoids the performance overhead of
	 * distinct sorting on simple single-table queries.</li>
	 * <li><b>Result Type Safety:</b> It ensures that {@code DISTINCT} is not
	 * applied to count queries (where the result type is {@link Long}), preventing
	 * potential JPA provider exceptions and incorrect metadata counts.</li>
	 * </ol>
	 * <p>
	 * <b>When to use:</b> Use this when building user-facing search queries that
	 * navigate {@code OneToMany} or {@code ManyToMany} relationships, where a
	 * single parent entity would otherwise appear multiple times in the result list due to
	 * multiple matching child records.
	 * </p>
	 * <b>Example:</b>
	 *
	 * <pre>{@code
	 * Specification<Customer> spec = smartDistinct(hasRecentOrder().and(isPremium()));
	 * List<Customer> results = repository.findAll(spec);
	 * }</pre>
	 *
	 * @param <T> The type of the entity the specification is checking.
	 * @param spec The underlying specification to execute.
	 * @return A new Specification that conditionally applies {@code DISTINCT}.
	 */
	public static <T> @NonNull Specification<T> smartDistinct(Specification<T> spec) {
		return (root, query, cb) -> {
			Predicate predicate = spec.toPredicate(root, query, cb);

			if (!root.getJoins().isEmpty()) {
				Class<?> resultType = query.getResultType();
				if (resultType != Long.class && resultType != long.class) {
					query.distinct(true);
				}
			}
			return predicate;
		};
	}

	/**
	 * Returns an unrestricted specification that acts as a safe, backward-compatible replacement
	 * for returning {@code null} out of specification factories, which Spring Boot 4's {@code findAll()}
	 * no longer accepts.
	 * <p>
	 * This method exists for Spring Boot 3 support; Sprnig Boot 4 has it's own version of this
	 * (see {@link Specification#unrestricted()}.
	 *
	 * @param <T> The entity type being queried.
	 */
	public static <T> @NonNull Specification<T> unrestricted() {
		return (root, query, cb) -> null;
	}

}

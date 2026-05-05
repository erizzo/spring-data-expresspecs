package expresspecs;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class SpecificationExtensions {

	/**
	 * Null-safe or() for Specifications that tolerates null values for either argument
	 */
	public static <T> Specification<T> safeOr(@Nullable Specification<T> existing, @Nullable Specification<T> additional) {
		if (existing == null) {
			return additional;
		}

		if (additional == null) {
			return existing;
		}

		return existing.or(additional);
	}

	/**
	 * Null-safe and() for Specifications that tolerates null values for either argument
	 */
	public static <T> Specification<T> safeAnd(@Nullable Specification<T> existing, @Nullable Specification<T> additional) {
		if (existing == null) {
			return additional;
		}

		if (additional == null) {
			return existing;
		}

		return existing.and(additional);
	}

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
}
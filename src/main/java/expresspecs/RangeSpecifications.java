package expresspecs;

import static expresspecs.BasicSpecifications.unrestricted;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;
import lombok.experimental.UtilityClass;

/**
 * Predicate factories for Range-based JPA Specifications, such as comparisons and bounds.
 */
@UtilityClass
public class RangeSpecifications {

	/**
	 * Creates a specification that matches entities where the specified property is strictly less than {@code value},
	 * or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification} if {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> lessThan(String propertyPath, C value) {
		return lessThan(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property is strictly less than {@code value},
	 * or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification} if {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Value to compare against.
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> lessThan(PropertyPath propertyPath, C value) {
		if (value == null) {
			return unrestricted();
		}
		return (root, query, cb) -> {
			Path<C> path = propertyPath.asPath(root);
			return cb.lessThan(path, value);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is strictly greater than {@code value},
	 * or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification} if {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> greaterThan(String propertyPath, C value) {
		return greaterThan(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property is strictly greater than {@code value},
	 * or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification} if {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Value to compare against.
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> greaterThan(PropertyPath propertyPath, C value) {
		if (value == null) {
			return unrestricted();
		}
		return (root, query, cb) -> {
			Path<C> path = propertyPath.asPath(root);
			return cb.greaterThan(path, value);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is greater than or equal to
	 * {@code value}, or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification} if
	 * {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> atLeast(String propertyPath, C value) {
		return atLeast(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property is greater than or equal to
	 * {@code value}, or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification} if
	 * {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Value to compare against.
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> atLeast(PropertyPath propertyPath, C value) {
		if (value == null) {
			return unrestricted();
		}
		return (root, query, cb) -> {
			Path<C> path = propertyPath.asPath(root);
			return cb.greaterThanOrEqualTo(path, value);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is less than or equal to
	 * {@code value}, or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification} if
	 * {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> atMost(String propertyPath, C value) {
		return atMost(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property is less than or equal to
	 * {@code value}, or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification} if
	 * {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Value to compare against.
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> atMost(PropertyPath propertyPath, C value) {
		if (value == null) {
			return unrestricted();
		}
		return (root, query, cb) -> {
			Path<C> path = propertyPath.asPath(root);
			return cb.lessThanOrEqualTo(path, value);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is greater than or
	 * equal to {@code startInclusive} and strictly less than {@code endExclusive}.
	 *
	 * <p>If both bounds are {@code null}, an {@linkplain BasicSpecifications#unrestricted() unrestricted
	 * specification} is returned, consistent with other factories in this class.
	 *
	 * <p>Unlike other factories in this class, a single {@code null} bound is not permitted and will throw
	 * {@link IllegalArgumentException}. This is intentional: because this method delegates to
	 * {@link #atLeast} and {@link #lessThan}, a null bound would silently produce a one-sided range
	 * rather than the two-sided range the caller requested, hiding a subtle behavioral change in the
	 * resulting query. If only one bound is needed, call {@link #atLeast} or {@link #lessThan} directly.
	 *
	 * @param <T>            The entity type being queried.
	 * @param <C>            The comparable type of the property.
	 * @param propertyPath   Dot-delimited property path to compare.
	 * @param startInclusive The inclusive lower bound; may be {@code null} only if {@code endExclusive} is also {@code null}.
	 * @param endExclusive   The exclusive upper bound; may be {@code null} only if {@code startInclusive} is also {@code null}.
	 * @throws IllegalArgumentException if exactly one bound is {@code null}.
	 * @see PropertyPath#from(String)
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> between(
			String propertyPath, C startInclusive, C endExclusive) {
		return between(PropertyPath.from(propertyPath), startInclusive, endExclusive);
	}

	/**
	 * Creates a specification that matches entities where the specified property is greater than or
	 * equal to {@code startInclusive} and strictly less than {@code endExclusive}.
	 *
	 * <p>If both bounds are {@code null}, an {@linkplain BasicSpecifications#unrestricted() unrestricted
	 * specification} is returned, consistent with other factories in this class.
	 *
	 * <p>Unlike other factories in this class, a single {@code null} bound is not permitted and will throw
	 * {@link IllegalArgumentException}. This is intentional: because this method delegates to
	 * {@link #atLeast} and {@link #lessThan}, a null bound would silently produce a one-sided range
	 * rather than the two-sided range the caller requested, hiding a subtle behavioral change in the
	 * resulting query. If only one bound is needed, call {@link #atLeast} or {@link #lessThan} directly.
	 *
	 * @param <T>            The entity type being queried.
	 * @param <C>            The comparable type of the property.
	 * @param propertyPath   Resolved property path to compare.
	 * @param startInclusive The inclusive lower bound; may be {@code null} only if {@code endExclusive} is also {@code null}.
	 * @param endExclusive   The exclusive upper bound; may be {@code null} only if {@code startInclusive} is also {@code null}.
	 * @throws IllegalArgumentException if exactly one bound is {@code null}.
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> between(
			PropertyPath propertyPath, C startInclusive, C endExclusive) {
		if (startInclusive == null && endExclusive == null) {
			return unrestricted();
		}
		if (startInclusive == null || endExclusive == null) {
			throw new IllegalArgumentException("between() requires both bounds or neither; use atLeast() or lessThan() for a one-sided range");
		}
		Specification<T> afterStart = atLeast(propertyPath, startInclusive);
		Specification<T> beforeEnd = lessThan(propertyPath, endExclusive);

		return afterStart.and(beforeEnd);
	}

}

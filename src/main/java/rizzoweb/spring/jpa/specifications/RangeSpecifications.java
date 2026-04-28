package rizzoweb.spring.jpa.specifications;

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
	 * Creates a specification that matches entities where the specified property is
	 * strictly less than
	 * {@code value}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> lessThan(String propertyPath,
			C value) {
		return lessThan(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property is
	 * strictly less than
	 * {@code value}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Value to compare against.
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> lessThan(PropertyPath propertyPath,
			C value) {
		return (root, query, cb) -> {
			Path<C> path = propertyPath.asPath(root);
			return cb.lessThan(path, value);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is strictly greater
	 * than {@code value}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> greaterThan(String propertyPath,
			C value) {
		return greaterThan(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property is strictly greater
	 * than {@code value}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Value to compare against.
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> greaterThan(PropertyPath propertyPath,
			C value) {
		return (root, query, cb) -> {
			Path<C> path = propertyPath.asPath(root);
			return cb.greaterThan(path, value);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is greater than or
	 * equal to {@code value}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> atLeast(String propertyPath, C value) {
		return atLeast(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property is greater than or
	 * equal to {@code value}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <C>          The comparable type of the property.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Value to compare against.
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> atLeast(PropertyPath propertyPath,
			C value) {
		return (root, query, cb) -> {
			Path<C> path = propertyPath.asPath(root);
			return cb.greaterThanOrEqualTo(path, value);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is greater than or
	 * equal to {@code startInclusive} and strictly less than {@code endExclusive}.
	 *
	 * @param <T>            The entity type being queried.
	 * @param <C>            The comparable type of the property.
	 * @param propertyPath   Dot-delimited property path to compare.
	 * @param startInclusive The inclusive lower bound.
	 * @param endExclusive   The exclusive upper bound.
	 * @see PropertyPath#from(String)
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> between(String propertyPath,
			C startInclusive, C endExclusive) {
		return between(PropertyPath.from(propertyPath), startInclusive, endExclusive);
	}

	/**
	 * Creates a specification that matches entities where the specified property is greater than or
	 * equal to {@code startInclusive} and strictly less than {@code endExclusive}.
	 *
	 * @param <T>            The entity type being queried.
	 * @param <C>            The comparable type of the property.
	 * @param propertyPath   Resolved property path to compare.
	 * @param startInclusive The inclusive lower bound.
	 * @param endExclusive   The exclusive upper bound.
	 */
	public static <T, C extends Comparable<? super C>> @NonNull Specification<T> between(PropertyPath propertyPath,
			C startInclusive, C endExclusive) {
		Specification<T> afterStart = atLeast(propertyPath, startInclusive);
		Specification<T> beforeEnd = lessThan(propertyPath, endExclusive);

		return afterStart.and(beforeEnd);
	}

}

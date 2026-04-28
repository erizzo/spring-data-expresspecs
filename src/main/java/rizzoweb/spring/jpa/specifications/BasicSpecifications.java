package rizzoweb.spring.jpa.specifications;

import static rizzoweb.spring.jpa.specifications.JPASpecificationUtils.unrestricted;

import java.util.Collection;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import lombok.experimental.UtilityClass;

/**
 * Basic predicate factories for JPA Specifications, such as equality, null checks, and boolean logic.
 */
@UtilityClass
public class BasicSpecifications {

	/**
	 * Creates a specification that matches entities where the specified property is {@code true}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to a boolean attribute.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> isTrue(String propertyPath) {
		return isTrue(PropertyPath.from(propertyPath));
	}

	/**
	 * Creates a specification that matches entities where the specified property is {@code true}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to a boolean attribute.
	 */
	public static <T> @NonNull Specification<T> isTrue(PropertyPath propertyPath) {
		return (root, query, cb) -> {
			Path<Boolean> path = propertyPath.asPath(root);
			return cb.isTrue(path);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is {@code false}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to a boolean attribute.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> isFalse(String propertyPath) {
		return isFalse(PropertyPath.from(propertyPath));
	}

	/**
	 * Creates a specification that matches entities where the specified property is {@code false}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to a boolean attribute.
	 */
	public static <T> @NonNull Specification<T> isFalse(PropertyPath propertyPath) {
		return (root, query, cb) -> {
			Path<Boolean> path = propertyPath.asPath(root);
			return cb.isFalse(path);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to test for null.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> isNull(String propertyPath) {
		return isNull(PropertyPath.from(propertyPath));
	}

	/**
	 * Creates a specification that matches entities where the specified property is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to test for null.
	 */
	public static <T> @NonNull Specification<T> isNull(PropertyPath propertyPath) {
		return (root, query, cb) -> {
			Path<?> path = propertyPath.asPath(root);
			return cb.isNull(path);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property is not {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to test for non-null.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> notNull(String propertyPath) {
		return notNull(PropertyPath.from(propertyPath));
	}

	/**
	 * Creates a specification that matches entities where the specified property is not {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to test for non-null.
	 */
	public static <T> @NonNull Specification<T> notNull(PropertyPath propertyPath) {
		return (root, query, cb) -> {
			Path<?> path = propertyPath.asPath(root);
			return cb.isNotNull(path);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property equals {@code value}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The property value type.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T, V> @NonNull Specification<T> is(String propertyPath, V value) {
		return is(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property equals {@code value}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The property value type.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Value to compare against.
	 */
	public static <T, V> @NonNull Specification<T> is(PropertyPath propertyPath, V value) {
		if (ObjectUtils.isEmpty(value)) {
			return unrestricted();
		}

		return (root, query, cb) -> {
			Path<?> path = propertyPath.asPath(root);
			return cb.equal(path, value);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property does not equal {@code aValue}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The property value type.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param aValue       Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T, V> @NonNull Specification<T> isNot(String propertyPath, V aValue) {
		return isNot(PropertyPath.from(propertyPath), aValue);
	}

	/**
	 * Creates a specification that matches entities where the specified property does not equal {@code aValue}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The property value type.
	 * @param propertyPath Resolved property path to compare.
	 * @param aValue       Value to compare against.
	 */
	public static <T, V> @NonNull Specification<T> isNot(PropertyPath propertyPath, V aValue) {
		if (ObjectUtils.isEmpty(aValue)) {
			return unrestricted();
		}

		return (root, query, cb) -> {
			Path<?> path = propertyPath.asPath(root);
			return cb.notEqual(path, aValue);
		};
	}

	/**
	 * Creates a specification that matches entities where two properties are equal.
	 *
	 * @param <T>   The entity type being queried.
	 * @param path1 First resolved property path in the comparison.
	 * @param path2 Second resolved property path in the comparison.
	 */
	public static <T> @NonNull Specification<T> areEqual(PropertyPath path1, PropertyPath path2) {
		return (root, query, cb) -> cb.equal(path1.asPath(root), path2.asPath(root));
	}

	/**
	 * Creates a specification that matches entities where two properties are equal.
	 *
	 * @param <T>       The entity type being queried.
	 * @param property1 Dot-delimited path for the first property in the comparison.
	 * @param property2 Dot-delimited path for the second property in the comparison.
	 */
	public static <T> @NonNull Specification<T> areEqual(String property1, String property2) {
		return areEqual(PropertyPath.from(property1), PropertyPath.from(property2));
	}

	/**
	 * Creates a specification that matches entities where the specified property equals any
	 * value in {@code searchValues}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The property value type.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param searchValues Candidate values for an {@code IN (...)} predicate.
	 * @see PropertyPath#from(String)
	 */
	public static <T, V> @NonNull Specification<T> isAny(String propertyPath, Collection<V> searchValues) {
		return isAny(PropertyPath.from(propertyPath), searchValues);
	}

	/**
	 * Creates a specification that matches entities where the specified property equals any
	 * value in {@code searchValues}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The property value type.
	 * @param propertyPath Resolved property path to compare.
	 * @param searchValues Candidate values for an {@code IN (...)} predicate.
	 */
	public static <T, V> @NonNull Specification<T> isAny(PropertyPath propertyPath, Collection<V> searchValues) {
		if (CollectionUtils.isEmpty(searchValues)) {
			return unrestricted();
		}

		return (root, query, cb) -> {
			Path<V> path = propertyPath.asPath(root);
			CriteriaBuilder.In<V> in = cb.in(path);
			for (V v : searchValues) {
				in.value(v);
			}
			return in;
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property does not
	 * equal any value in {@code searchValues}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The property value type.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param searchValues Candidate values for a {@code NOT IN (...)} predicate.
	 * @see PropertyPath#from(String)
	 */
	public static <T, V> @NonNull Specification<T> isNotAny(String propertyPath, Collection<V> searchValues) {
		return isNotAny(PropertyPath.from(propertyPath), searchValues);
	}

	/**
	 * Creates a specification that matches entities where the specified property does not
	 * equal any value in {@code searchValues}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The property value type.
	 * @param propertyPath Resolved property path to compare.
	 * @param searchValues Candidate values for a {@code NOT IN (...)} predicate.
	 */
	public static <T, V> @NonNull Specification<T> isNotAny(PropertyPath propertyPath, Collection<V> searchValues) {
		if (CollectionUtils.isEmpty(searchValues)) {
			return unrestricted();
		}

		return (root, query, cb) -> {
			Path<V> path = propertyPath.asPath(root);
			CriteriaBuilder.In<V> in = cb.in(path);
			for (V v : searchValues) {
				in.value(v);
			}
			return cb.not(in);
		};
	}

}

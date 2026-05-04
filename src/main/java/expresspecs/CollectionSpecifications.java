package expresspecs;

import static expresspecs.BasicSpecifications.unrestricted;

import java.util.Collection;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;
import lombok.experimental.UtilityClass;

/**
 * Predicate factories for Collection-based JPA Specifications, such as membership and size checks.
 */
@UtilityClass
public class CollectionSpecifications {

	/**
	 * Creates a specification that matches entities where the specified collection property
	 * is not empty (has at least one element).
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to a collection attribute.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> isNotEmpty(String propertyPath) {
		return isNotEmpty(PropertyPath.from(propertyPath));
	}

	/**
	 * Creates a specification that matches entities where the specified collection property
	 * is not empty (has at least one element).
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to a collection attribute.
	 */
	public static <T> @NonNull Specification<T> isNotEmpty(PropertyPath propertyPath) {
		return (root, query, cb) -> {
			Path<Collection<?>> path = propertyPath.asPath(root);
			return cb.isNotEmpty(path);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified collection property
	 * is empty (has no elements).
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to a collection attribute.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> isEmpty(String propertyPath) {
		return isEmpty(PropertyPath.from(propertyPath));
	}

	/**
	 * Creates a specification that matches entities where the specified collection property
	 * is empty (has no elements).
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to a collection attribute.
	 */
	public static <T> @NonNull Specification<T> isEmpty(PropertyPath propertyPath) {
		return (root, query, cb) -> {
			Path<Collection<?>> path = propertyPath.asPath(root);
			return cb.isEmpty(path);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified collection property
	 * contains {@code value}, or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification}
	 * if {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The element type in the collection.
	 * @param propertyPath Dot-delimited property path to a collection attribute.
	 * @param value        The value to check for membership.
	 * @see PropertyPath#from(String)
	 */
	public static <T, V> @NonNull Specification<T> containsMember(String propertyPath, V value) {
		return containsMember(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified collection property
	 * contains {@code value}, or an {@linkplain BasicSpecifications#unrestricted() unrestricted specification}
	 * if {@code value} is {@code null}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param <V>          The element type in the collection.
	 * @param propertyPath Resolved property path to a collection attribute.
	 * @param value        The value to check for membership.
	 */
	public static <T, V> @NonNull Specification<T> containsMember(PropertyPath propertyPath, V value) {
		if (value == null) {
			return unrestricted();
		}

		return (root, query, cb) -> {
			@SuppressWarnings("unchecked")
			Path<Collection<V>> path = (Path<Collection<V>>) (Path<?>) propertyPath.asPath(root);
			return cb.isMember(value, path);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified collection property
	 * has at least {@code minSize} elements.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to a collection attribute.
	 * @param minSize      The minimum number of elements required.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> sizeAtLeast(String propertyPath, int minSize) {
		return sizeAtLeast(PropertyPath.from(propertyPath), minSize);
	}

	/**
	 * Creates a specification that matches entities where the specified collection property
	 * has at least {@code minSize} elements.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to a collection attribute.
	 * @param minSize      The minimum number of elements required.
	 */
	public static <T> @NonNull Specification<T> sizeAtLeast(PropertyPath propertyPath, int minSize) {
		return (root, query, cb) -> {
			Path<Collection<?>> path = propertyPath.asPath(root);
			return cb.greaterThanOrEqualTo(cb.size(path), minSize);
		};
	}

}

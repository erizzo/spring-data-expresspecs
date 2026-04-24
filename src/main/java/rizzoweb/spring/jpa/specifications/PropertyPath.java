package rizzoweb.spring.jpa.specifications;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;


import java.util.List;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.ManagedType;

/**
 * Represents a dot-separated property path for use in JPA Criteria queries.
 *
 * <p>A {@code PropertyPath} models a chain of property names (e.g., {@code "address.city"})
 * and can resolve itself into a JPA {@link Path} via {@link #asPath(Root)}.
 *
 * <p>During resolution, intermediate segments that are entity associations ({@code @ManyToOne},
 * {@code @OneToOne}, etc.) are traversed using LEFT JOINs, while embeddable or simple
 * intermediate segments use standard path navigation. The final (leaf) segment is always
 * resolved with {@code get()} — it is assumed to be the property being used in a predicate,
 * not a relationship being navigated through. This means the leaf should be a simple property,
 * an embeddable, or an association compared by its foreign key (e.g., {@code cb.equal()} or
 * {@code cb.isNull()}).
 */
public record PropertyPath(List<String> properties) {

	/**
	 * Factory method to create a path from one or more segments.
	 */
	public static PropertyPath of(String... properties) {
		if (isEmpty(properties)) {
			throw new IllegalArgumentException("PropertyPath must contain at least one property");
		}

		return new PropertyPath(List.of(properties));
	}

	/**
	 * Factory method to construct a path from a dot-separated string.
	 * Example: PropertyPath.fromDotSeparated("address.city")
	 */
	public static PropertyPath from(String path) {
		if (isBlank(path)) {
			throw new IllegalArgumentException("Path string must not be blank");
		}

		final String[] properties = path.split("\\.");
		return new PropertyPath(List.of(properties));
	}


	@SuppressWarnings("unchecked")
	public <V> Path<V> asPath(Root<?> root) {
		List<String> intermediates = properties.subList(0, properties.size() - 1);
		String leaf = properties.get(properties.size() - 1);

		// Navigate through intermediate segments (associations join, embeddables get)
		Path<?> current = root;
		for (String segment : intermediates) {
			if (isAssociation(current, segment)) {
				current = findOrCreateJoin((From<?, ?>) current, segment);
			} else {
				current = current.get(segment);
			}
		}

		// Resolve the leaf segment
		return (Path<V>) current.get(leaf);
	}

	private boolean isAssociation(Path<?> path, String segment) {
		var model = path.getModel();

		if (model instanceof ManagedType<?> managedType) {
			return managedType.getAttribute(segment).isAssociation();
		}

		return false;
	}

	private From<?, ?> findOrCreateJoin(From<?, ?> from, String segment) {
		return from.getJoins().stream()
				.filter(j -> j.getAttribute().getName().equals(segment))
				.findFirst()
				.map(j -> (From<?, ?>) j)
				.orElseGet(() -> from.join(segment, JoinType.LEFT));
	}

}
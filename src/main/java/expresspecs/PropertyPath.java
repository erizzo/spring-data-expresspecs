package expresspecs;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;


import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

/**
 * Represents a path through a JPA entity graph for use in Criteria queries.
 *
 * <p>A {@code PropertyPath} models a chain of attribute names traversing from a root entity
 * through zero or more intermediate associations or embeddables to a leaf attribute. It can be
 * constructed from dot-separated strings (e.g., {@code "address.city"}), string varargs, or
 * JPA metamodel attributes (e.g., {@code Customer_.address}, {@code Address_.city}), and
 * resolves itself into a JPA {@link Path} via {@link #asPath(Root)}.
 *
 * <p>During resolution, intermediate segments that are entity associations ({@code @ManyToOne},
 * {@code @OneToOne}, etc.) are traversed using LEFT JOINs, while embeddable or simple
 * intermediate segments use standard path navigation. The final (leaf) segment is always
 * resolved with {@code get()}, it is assumed to be the property being used in a predicate,
 * not a relationship being navigated through. This means the leaf should be a simple property,
 * an embeddable, or an association compared by its foreign key (e.g., {@code cb.equal()} or
 * {@code cb.isNull()}).
 */
public record PropertyPath(List<String> properties) {

	/** Exists solely to resolve the ambiguity between the two varargs overloads when called with no arguments. */
	public static PropertyPath of() {
		throw new IllegalArgumentException("PropertyPath must contain at least one property");
	}

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


	/**
	 * Factory method to create a path from one or more JPA metamodel attributes.
	 *
	 * <p>Accepts any mix of attribute types, including plural (collection) attributes.
	 * The attribute names are extracted in order to form the path. This overload provides
	 * refactoring safety: a renamed entity field breaks compilation here, but does not
	 * enforce that the attribute types form a valid chain. For chain-type validation,
	 * use the typed two- or three-segment overloads.
	 *
	 * <p>Example: {@code PropertyPath.of(Customer_.address, Address_.zipCode)}
	 */
	public static PropertyPath of(Attribute<?, ?>... attributes) {
		if (isEmpty(attributes)) {
			throw new IllegalArgumentException("PropertyPath must contain at least one attribute");
		}
		return new PropertyPath(Arrays.stream(attributes).map(Attribute::getName).toList());
	}

	/**
	 * Type-safe two-segment path through a singular (to-one) association or embeddable.
	 *
	 * <p>The compiler verifies that {@code second} belongs to the type produced by {@code first}.
	 *
	 * <p>Example: {@code PropertyPath.of(Customer_.address, Address_.zipCode)}
	 */
	public static <X, Y, Z> PropertyPath of(SingularAttribute<X, Y> first, Attribute<Y, Z> second) {
		return new PropertyPath(List.of(first.getName(), second.getName()));
	}

	/**
	 * Type-safe two-segment path through a plural (to-many) association.
	 *
	 * <p>The compiler verifies that {@code second} belongs to the element type of the collection
	 * produced by {@code first}, not the collection type itself.
	 *
	 * <p>Example: {@code PropertyPath.of(Customer_.orders, Order_.datePlaced)}
	 */
	public static <X, E, Z> PropertyPath of(PluralAttribute<X, ?, E> first, Attribute<E, Z> second) {
		return new PropertyPath(List.of(first.getName(), second.getName()));
	}

	/**
	 * Type-safe three-segment path through two singular associations or embeddables.
	 *
	 * <p>Example: {@code PropertyPath.of(Customer_.address, Address_.region, Region_.name)}
	 */
	public static <X, Y, Z, W> PropertyPath of(SingularAttribute<X, Y> first, SingularAttribute<Y, Z> second, Attribute<Z, W> third) {
		return new PropertyPath(List.of(first.getName(), second.getName(), third.getName()));
	}

	/**
	 * Type-safe three-segment path through a singular association followed by a plural one.
	 *
	 * <p>Example: {@code PropertyPath.of(Invoice_.customer, Customer_.orders, Order_.datePlaced)}
	 */
	public static <X, Y, E, W> PropertyPath of(SingularAttribute<X, Y> first, PluralAttribute<Y, ?, E> second, Attribute<E, W> third) {
		return new PropertyPath(List.of(first.getName(), second.getName(), third.getName()));
	}

	@Override
	public String toString() {
		return StringUtils.join(properties, '.');
	}

	@SuppressWarnings("unchecked")
	public <V> Path<V> asPath(Root<?> root) {
		List<String> intermediates = properties.subList(0, properties.size() - 1);
		String leaf = properties.get(properties.size() - 1);

		// Navigate through intermediate segments (associations join, embeddables get)
		Path<?> current = root;
		for (String segment : intermediates) {
			if (isAssociation(current, segment)) {
				// When encountering an association (relation to another entity), we will
				// reuse an existing JOIN if one exists
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

	/**
	 * Resolves an association segment by either reusing an existing join or creating a new one.
	 *
	 * <p>This exists to prevent redundant JOINs in the generated SQL when multiple Specification
	 * fragments navigate the same association path. Reusing joins ensures that criteria are
	 * applied to the same related record rather than potentially different records of the
	 * same type.</p>
	 *
	 * <p>It works by inspecting the existing joins on the {@code from} path. If a join for the
	 * specified {@code segment} already exists, it is returned. Otherwise, a new LEFT JOIN is
	 * initialized and added to the query.</p>
	 *
	 * @param from    The source path from which to join.
	 * @param segment The name of the association attribute.
	 */
	private From<?, ?> findOrCreateJoin(From<?, ?> from, String segment) {
		return from.getJoins().stream()
				.filter(j -> j.getAttribute().getName().equals(segment))
				.findFirst()
				.map(j -> (From<?, ?>) j)
				.orElseGet(() -> from.join(segment, JoinType.LEFT));
	}

}
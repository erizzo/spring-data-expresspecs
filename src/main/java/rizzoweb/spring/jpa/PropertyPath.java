package rizzoweb.spring.jpa;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Iterator;
import java.util.List;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.ManagedType;

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
        Path<?> current = root;
        Iterator<String> it = properties.iterator();

        while (it.hasNext()) {
            String segment = it.next();

            // If this is the last segment, return the final Path (the leaf)
            if (! it.hasNext()) {
                return (Path<V>) current.get(segment);
            }

            // Otherwise, navigate deeper
            if (isAssociation(current, segment)) {
                // Confirmation: only Entities (From nodes) can have associations
                current = findOrCreateJoin((From<?, ?>) current, segment);
            } else {
                // Handle @Embeddables or nested components via navigation
                current = current.get(segment);
            }
        }

        return (Path<V>) current;
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
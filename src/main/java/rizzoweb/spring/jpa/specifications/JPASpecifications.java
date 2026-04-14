package rizzoweb.spring.jpa.specifications;

import static rizzoweb.spring.jpa.specifications.SpecificationExtensions.safeOr;
import static rizzoweb.utils.SQLUtils.escapeLike;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;

@UtilityClass
@ExtensionMethod(SpecificationExtensions.class)
public class JPASpecifications {

    static final char ESCAPE_CHAR = '\\';

    /**
     * Wraps a {@link Specification} to ensure that results are distinct, but only when necessary.
     * * <p>This decorator performs two critical checks before applying a {@code DISTINCT} keyword:
     * <ol>
     * <li><b>Join Detection:</b> It only applies distinct logic if the query has actually
     * performed a {@code JOIN}. This avoids the performance overhead of distinct sorting
     * on simple single-table queries.</li>
     * <li><b>Result Type Safety:</b> It ensures that {@code DISTINCT} is not applied to
     * count queries (where the result type is {@link Long}), preventing potential
     * JPA provider exceptions and incorrect metadata counts.</li>
     * </ol>
     * <p><b>When to use:</b> Use this when building user-facing search queries that navigate
     * {@code OneToMany} or {@code ManyToMany} relationships, where a single parent entity
     * would otherwise appear multiple times in the result list due to multiple matching
     * child records.</p>
     * <b>Example:</b>
     * <pre>{@code  Specification<Customer> spec = smartDistinct(hasRecentOrder().and(isPremium()));
     * List<Customer> results = repository.findAll(spec);
     * }</pre>
     *
     * @param <T>  The type of the entity the specification is checking.
     * @param spec The underlying specification to execute.
     * @return A new Specification that conditionally applies {@code DISTINCT}.
     */
    public static <T> Specification<T> smartDistinct(Specification<T> spec) {
        return (root, query, cb) -> {
            Predicate predicate = spec.toPredicate(root, query, cb);

            if (query != null && !root.getJoins().isEmpty()) {
                Class<?> resultType = query.getResultType();
                if (resultType != Long.class && resultType != long.class) {
                    query.distinct(true);
                }
            }
            return predicate;
        };
    }

    public static <T> Specification<T> isTrue(String property) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get(property));
    }

    public static <T> Specification<T> isTrue(PropertyPath propertyPath) {
        return (root, query, cb) -> {
            Path<Boolean> path = propertyPath.asPath(root);
			return cb.isTrue(path);
        };
    }

    public static <T> Specification<T> isFalse(String property) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(property));
    }

    public static <T> Specification<T> isFalse(PropertyPath propertyPath) {
        return (root, query, cb) -> {
            Path<Boolean> path = propertyPath.asPath(root);
			return cb.isFalse(path);
        };
    }

    public static <T> Specification<T> isNull(String property) {
        return isNull(PropertyPath.from(property));
    }

    public static <T> Specification<T> isNull(PropertyPath propertyPath) {
        return (root, query, cb) -> {
            Path<Boolean> path = propertyPath.asPath(root);
			return cb.isNull(path);
        };
    }

    public static <T> Specification<T> notNull(String property) {
        return notNull(PropertyPath.from(property));
    }

    public static <T> Specification<T> notNull(PropertyPath propertyPath) {
        return (root, query, cb) -> {
            Path<Boolean> path = propertyPath.asPath(root);
			return cb.isNotNull(path);
        };
    }

    public static <T, V> Specification<T> is(String property, V value) {
        return is(PropertyPath.from(property), value);
    }

    public static <T, V> Specification<T> is(PropertyPath propertyPath, V value) {
        if (ObjectUtils.isEmpty(value)) { return null; }

        return (root, query, cb) -> {
            Path<?> path = propertyPath.asPath(root);
			return cb.equal(path, value);
        };
    }

    public static <T, V> Specification<T> isNot(String property, V aValue) {
    	return isNot(PropertyPath.from(property), aValue);
    }

    public static <T, V> Specification<T> isNot(PropertyPath propertyPath, V aValue) {
    	if (ObjectUtils.isEmpty(aValue)) { return null; }

    	return (root, query, cb) -> {
    		Path<?> path = propertyPath.asPath(root);
    		return cb.notEqual(path, aValue);
    	};
    }

    public static <T> Specification<T> areEqual(PropertyPath path1, PropertyPath path2) {
        return (root, query, cb) ->  cb.equal(path1.asPath(root), path2.asPath(root));
    }

    public static <T> Specification<T> areEqual(String property1, String property2) {
    	return areEqual(PropertyPath.from(property1), PropertyPath.from(property2));
    }


    public static <T, V> Specification<T> isAny(String property, Collection<V> searchValues) {
    	return isAny(PropertyPath.from(property), searchValues);
    }


    public static <T, V> Specification<T> isAny(PropertyPath propertyPath, Collection<V> searchValues) {
        if (isEmpty(searchValues)) { return null; }

        return (root, query, cb) -> {
            Path<Object> path = propertyPath.asPath(root);
            return cb.in(path).value(searchValues);
        };
    }


    public static <T> Specification<T> contains(String property, String value) {
        return contains(PropertyPath.from(property), value);
    }

    public static <T> Specification<T> contains(PropertyPath propertyPath, String value) {
    	if (isEmpty(value)) { return null; }

    	String escapedValue = escapeLike(value, ESCAPE_CHAR);
        return (root, query, cb) -> {
            Path<String> path = propertyPath.asPath(root);
            return cb.like(path, "%"+escapedValue+"%", ESCAPE_CHAR);
        };
    }

    public static <T> Specification<T> doesNotContain(String property, String value) {
        return doesNotContain(PropertyPath.from(property), value);
    }

    public static <T> Specification<T> doesNotContain(PropertyPath propertyPath, String value) {
    	if (isEmpty(value)) { return null; }

    	String escapedValue = escapeLike(value, ESCAPE_CHAR);
    	return (root, query, cb) -> {
    		Path<String> path = propertyPath.asPath(root);
    		return cb.notLike(path, "%"+escapedValue+"%", ESCAPE_CHAR);
    	};
    }


    public static <T> Specification<T> containsAny(String property, List<String> searchTerms) {
        return containsAny(PropertyPath.from(property), searchTerms);
    }

    public static <T> Specification<T> containsAny(PropertyPath propertyPath, List<String> searchTerms) {
        if (isEmpty(searchTerms)) { return null; }

        Specification<T> combinedSpec = null;

        for (String term : searchTerms) {
            Specification<T> termSpec = contains(propertyPath, term);
            combinedSpec = safeOr(combinedSpec, termSpec);
        }

        return combinedSpec;
    }

    public static <T> Specification<T> onDate(String property, LocalDate targetDate) {
    	return onDate(PropertyPath.from(property), targetDate);
    }

    public static <T> Specification<T> onDate(PropertyPath propertyPath, LocalDate targetDate) {
        if (targetDate == null) { return null; }

        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

        Specification<T> afterStart = atLeast(propertyPath, start);
        Specification<T> beforeEnd = lessThan(propertyPath, end);

        return afterStart.and(beforeEnd);
    }


    public static <T, C extends Comparable<? super C>> Specification<T> lessThan(String property, C value) {
        return lessThan(PropertyPath.from(property), value);
    }

    public static <T, C extends Comparable<? super C>> Specification<T> lessThan(PropertyPath propertyPath, C value) {
        return (root, query, cb) -> {
            Path<C> path = propertyPath.asPath(root);
            return cb.lessThan(path, value);
        };
    }

    public static <T, C extends Comparable<? super C>> Specification<T> greaterThan(String property, C value) {
        return greaterThan(PropertyPath.from(property), value);
    }

    public static <T, C extends Comparable<? super C>> Specification<T> greaterThan(PropertyPath propertyPath, C value) {
        return (root, query, cb) -> {
            Path<C> path = propertyPath.asPath(root);
            return cb.greaterThan(path, value);
        };
    }

    public static <T, C extends Comparable<? super C>> Specification<T> atLeast(String property, C value) {
    	return atLeast(PropertyPath.from(property), value);
    }

    public static <T, C extends Comparable<? super C>> Specification<T> atLeast(PropertyPath propertyPath, C value) {
        return (root, query, cb) -> {
            Path<C> path = propertyPath.asPath(root);
            return cb.greaterThanOrEqualTo(path, value);
        };
    }

    public static <T, C extends Comparable<? super C>> Specification<T> between(String property, C startInclusive, C endExclusive) {
    	return between(PropertyPath.from(property), startInclusive, endExclusive);
    }

    public static <T, C extends Comparable<? super C>> Specification<T> between(PropertyPath propertyPath, C startInclusive, C endExclusive) {
        Specification<T> afterStart = atLeast(propertyPath, startInclusive);
        Specification<T> beforeEnd = lessThan(propertyPath, endExclusive);

        return afterStart.and(beforeEnd);
    }
}

package rizzoweb.spring.jpa.specifications;

import static rizzoweb.utils.SQLUtils.escapeLike;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
     * <p>This decorator performs two critical checks before applying a {@code DISTINCT} keyword:
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

    /**
     * Creates a specification that matches entities where the specified property is
     * {@code true}.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to a boolean attribute.
     * @return A specification that evaluates to {@code true} when the target property is true.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> isTrue(String propertyPath) {
        return isTrue(PropertyPath.from(propertyPath));
    }

    /**
     * Creates a specification that matches entities where the specified property is
     * {@code true}.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to a boolean attribute.
     * @return A specification that evaluates to {@code true} when the target property is true.
     */
    public static <T> Specification<T> isTrue(PropertyPath propertyPath) {
        return (root, query, cb) -> {
            Path<Boolean> path = propertyPath.asPath(root);
			return cb.isTrue(path);
        };
    }

    /**
     * Creates a specification that matches entities where the specified property is
     * {@code false}.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to a boolean attribute.
     * @return A specification that evaluates to {@code true} when the target property is false.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> isFalse(String propertyPath) {
        return isFalse(PropertyPath.from(propertyPath));
    }

    /**
     * Creates a specification that matches entities where the specified property is
     * {@code false}.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to a boolean attribute.
     * @return A specification that evaluates to {@code true} when the target property is false.
     */
    public static <T> Specification<T> isFalse(PropertyPath propertyPath) {
        return (root, query, cb) -> {
            Path<Boolean> path = propertyPath.asPath(root);
			return cb.isFalse(path);
        };
    }

    /**
     * Creates a specification that matches entities where the specified property is {@code null}.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to test for null.
     * @return A specification that evaluates to {@code true} when the target property is null.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> isNull(String propertyPath) {
        return isNull(PropertyPath.from(propertyPath));
    }

    /**
     * Creates a specification that matches entities where the specified property is {@code null}.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to test for null.
     * @return A specification that evaluates to {@code true} when the target property is null.
     */
    public static <T> Specification<T> isNull(PropertyPath propertyPath) {
        return (root, query, cb) -> {
            Path<Boolean> path = propertyPath.asPath(root);
			return cb.isNull(path);
        };
    }

    /**
     * Creates a specification that matches entities where the specified property is not
     * {@code null}.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to test for non-null.
     * @return A specification that evaluates to {@code true} when the target property is not null.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> notNull(String propertyPath) {
        return notNull(PropertyPath.from(propertyPath));
    }

    /**
     * Creates a specification that matches entities where the specified property is not
     * {@code null}.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to test for non-null.
     * @return A specification that evaluates to {@code true} when the target property is not null.
     */
    public static <T> Specification<T> notNull(PropertyPath propertyPath) {
        return (root, query, cb) -> {
            Path<Boolean> path = propertyPath.asPath(root);
			return cb.isNotNull(path);
        };
    }

    /**
     * Creates a specification that matches entities where the specified property equals
     * {@code value}.
     *
     * @param <T> The entity type being queried.
     * @param <V> The property value type.
     * @param propertyPath Dot-delimited property path to compare.
     * @param value Value to compare against.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     * @see PropertyPath#from(String)
     */
    public static <T, V> Specification<T> is(String propertyPath, V value) {
        return is(PropertyPath.from(propertyPath), value);
    }

    /**
     * Creates a specification that matches entities where the specified property equals
     * {@code value}.
     *
     * @param <T> The entity type being queried.
     * @param <V> The property value type.
     * @param propertyPath Resolved property path to compare.
     * @param value Value to compare against.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     */
    public static <T, V> Specification<T> is(PropertyPath propertyPath, V value) {
        if (ObjectUtils.isEmpty(value)) { return null; }

        return (root, query, cb) -> {
            Path<?> path = propertyPath.asPath(root);
			return cb.equal(path, value);
        };
    }

    /**
     * Creates a specification that matches entities where the specified property does not
     * equal {@code aValue}.
     *
     * @param <T> The entity type being queried.
     * @param <V> The property value type.
     * @param propertyPath Dot-delimited property path to compare.
     * @param aValue Value to compare against.
     * @return A specification, or {@code null} when {@code aValue} is null/empty.
     * @see PropertyPath#from(String)
     */
    public static <T, V> Specification<T> isNot(String propertyPath, V aValue) {
    	return isNot(PropertyPath.from(propertyPath), aValue);
    }

    /**
     * Creates a specification that matches entities where the specified property does not
     * equal {@code aValue}.
     *
     * @param <T> The entity type being queried.
     * @param <V> The property value type.
     * @param propertyPath Resolved property path to compare.
     * @param aValue Value to compare against.
     * @return A specification, or {@code null} when {@code aValue} is null/empty.
     */
    public static <T, V> Specification<T> isNot(PropertyPath propertyPath, V aValue) {
    	if (ObjectUtils.isEmpty(aValue)) { return null; }

    	return (root, query, cb) -> {
    		Path<?> path = propertyPath.asPath(root);
    		return cb.notEqual(path, aValue);
    	};
    }

    /**
     * Creates a specification that matches entities where two properties are equal.
     *
     * @param <T> The entity type being queried.
     * @param path1 First resolved property path in the comparison.
     * @param path2 Second resolved property path in the comparison.
     * @return A specification that evaluates to {@code true} when both properties have
     *         equal values.
     */
    public static <T> Specification<T> areEqual(PropertyPath path1, PropertyPath path2) {
        return (root, query, cb) ->  cb.equal(path1.asPath(root), path2.asPath(root));
    }

    /**
     * Creates a specification that matches entities where two properties are equal.
     *
     * @param <T> The entity type being queried.
     * @param property1 Dot-delimited path for the first property in the comparison.
     * @param property2 Dot-delimited path for the second property in the comparison.
     * @return A specification that evaluates to {@code true} when both properties have
     *         equal values.
     */
    public static <T> Specification<T> areEqual(String property1, String property2) {
    	return areEqual(PropertyPath.from(property1), PropertyPath.from(property2));
    }


    /**
     * Creates a specification that matches entities where the specified property equals any
     * value in {@code searchValues}.
     *
     * @param <T> The entity type being queried.
     * @param <V> The property value type.
     * @param propertyPath Dot-delimited property path to compare.
     * @param searchValues Candidate values for an {@code IN (...)} predicate.
     * @return A specification, or {@code null} when {@code searchValues} is null/empty.
     * @see PropertyPath#from(String)
     */
    public static <T, V> Specification<T> isAny(String propertyPath, Collection<V> searchValues) {
    	return isAny(PropertyPath.from(propertyPath), searchValues);
    }


    /**
     * Creates a specification that matches entities where the specified property equals any
     * value in {@code searchValues}.
     *
     * @param <T> The entity type being queried.
     * @param <V> The property value type.
     * @param propertyPath Resolved property path to compare.
     * @param searchValues Candidate values for an {@code IN (...)} predicate.
     * @return A specification, or {@code null} when {@code searchValues} is null/empty.
     */
    public static <T, V> Specification<T> isAny(PropertyPath propertyPath, Collection<V> searchValues) {
        if (isEmpty(searchValues)) { return null; }

        return (root, query, cb) -> {
            Path<Object> path = propertyPath.asPath(root);
            return cb.in(path).value(searchValues);
        };
    }


    /**
     * Creates a specification that matches entities where the specified property contains
     * {@code value}. Matching is case-sensitive.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to compare.
     * @param value Substring to match within the property value.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> contains(String propertyPath, String value) {
        return contains(PropertyPath.from(propertyPath), value);
    }

    /**
     * Creates a specification that matches entities where the specified property contains
     * {@code value}. Matching is case-sensitive.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to compare.
     * @param value Substring to match within the property value.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     */
    public static <T> Specification<T> contains(PropertyPath propertyPath, String value) {
    	if (isEmpty(value)) { return null; }

    	String escapedValue = escapeLike(value, ESCAPE_CHAR);
        return (root, query, cb) -> {
            Path<String> path = propertyPath.asPath(root);
            return cb.like(path, "%"+escapedValue+"%", ESCAPE_CHAR);
        };
    }

    /**
     * Creates a specification that matches entities where the specified property contains
     * {@code value}. Matching ignores case.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to compare.
     * @param value Substring to match within the property value.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> containsIgnoreCase(String propertyPath, String value) {
        return containsIgnoreCase(PropertyPath.from(propertyPath), value);
    }

    /**
     * Creates a specification that matches entities where the specified property contains
     * {@code value}. Matching ignores case.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to compare.
     * @param value Substring to match within the property value.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     */
    public static <T> Specification<T> containsIgnoreCase(PropertyPath propertyPath, String value) {
        if (isEmpty(value)) { return null; }

        String escapedValue = escapeLike(value.toLowerCase(Locale.ROOT), ESCAPE_CHAR);
        return (root, query, cb) -> {
            Path<String> path = propertyPath.asPath(root);
            return cb.like(cb.lower(path), "%" + escapedValue + "%", ESCAPE_CHAR);
        };
    }

    /**
     * Creates a specification that matches entities where the specified property does not
     * contain {@code value}. Matching is case-sensitive.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to compare.
     * @param value Substring to exclude from the property value.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> doesNotContain(String propertyPath, String value) {
        return doesNotContain(PropertyPath.from(propertyPath), value);
    }

    /**
     * Creates a specification that matches entities where the specified property does not
     * contain {@code value}. Matching is case-sensitive.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to compare.
     * @param value Substring to exclude from the property value.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     */
    public static <T> Specification<T> doesNotContain(PropertyPath propertyPath, String value) {
    	if (isEmpty(value)) { return null; }

    	String escapedValue = escapeLike(value, ESCAPE_CHAR);
    	return (root, query, cb) -> {
    		Path<String> path = propertyPath.asPath(root);
    		return cb.notLike(path, "%"+escapedValue+"%", ESCAPE_CHAR);
    	};
    }

    /**
     * Creates a specification that matches entities where the specified property does not
     * contain {@code value}. Matching ignores case.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to compare.
     * @param value Substring to exclude from the property value.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> doesNotContainIgnoreCase(String propertyPath, String value) {
        return doesNotContainIgnoreCase(PropertyPath.from(propertyPath), value);
    }

    /**
     * Creates a specification that matches entities where the specified property does not
     * contain {@code value}. Matching ignores case.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to compare.
     * @param value Substring to exclude from the property value.
     * @return A specification, or {@code null} when {@code value} is null/empty.
     */
    public static <T> Specification<T> doesNotContainIgnoreCase(PropertyPath propertyPath, String value) {
        if (isEmpty(value)) { return null; }

        String escapedValue = escapeLike(value.toLowerCase(Locale.ROOT), ESCAPE_CHAR);
        return (root, query, cb) -> {
            Path<String> path = propertyPath.asPath(root);
            return cb.notLike(cb.lower(path), "%" + escapedValue + "%", ESCAPE_CHAR);
        };
    }


    /**
     * Creates a specification that matches entities where the specified property contains at
     * least one of the values in {@code searchTerms}. Matching ignores case.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to compare.
     * @param searchTerms Candidate substrings for case-insensitive matching.
     * @return A specification, or {@code null} when {@code searchTerms} is null/empty.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> containsAnyIgnoreCase(String propertyPath, List<String> searchTerms) {
        return containsAnyIgnoreCase(PropertyPath.from(propertyPath), searchTerms);
    }

    /**
     * Creates a specification that matches entities where the specified property contains at
     * least one of the values in {@code searchTerms}. Matching ignores case.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to compare.
     * @param searchTerms Candidate substrings for case-insensitive matching.
     * @return A specification, or {@code null} when {@code searchTerms} is null/empty.
     */
    public static <T> Specification<T> containsAnyIgnoreCase(PropertyPath propertyPath, List<String> searchTerms) {
        if (isEmpty(searchTerms)) { return null; }

        List<String> escapedTerms = searchTerms.stream()
                .filter(term -> !isEmpty(term))
                .map(term -> escapeLike(term.toLowerCase(Locale.ROOT), ESCAPE_CHAR))
                .toList();

        // Special case: all terms are empty so nothing to match against
        if (escapedTerms.isEmpty()) { return null; }

        return (root, query, cb) -> {
            Path<String> path = propertyPath.asPath(root);
            Predicate[] predicates = escapedTerms.stream()
                                        .map(term -> cb.like(cb.lower(path), "%" + term + "%", ESCAPE_CHAR))
                                        .toArray(Predicate[]::new);
            return cb.or(predicates);
        };
    }

    /**
     * Creates a specification that matches entities where the specified property contains at
     * least one of the values in {@code searchTerms}. Matching is case-sensitive.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Dot-delimited property path to compare.
     * @param searchTerms Candidate substrings for case-sensitive matching.
     * @return A specification, or {@code null} when {@code searchTerms} is null/empty.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> containsAny(String propertyPath, List<String> searchTerms) {
        return containsAny(PropertyPath.from(propertyPath), searchTerms);
    }

    /**
     * Creates a specification that matches entities where the specified property contains at
     * least one of the values in {@code searchTerms}. Matching is case-sensitive.
     *
     * @param <T> The entity type being queried.
     * @param propertyPath Resolved property path to compare.
     * @param searchTerms Candidate substrings for case-sensitive matching.
     * @return A specification, or {@code null} when {@code searchTerms} is null/empty.
     */
    public static <T> Specification<T> containsAny(PropertyPath propertyPath, List<String> searchTerms) {
        if (isEmpty(searchTerms)) { return null; }

        List<String> escapedTerms = searchTerms.stream()
                .filter(term -> !isEmpty(term))
                .map(term -> escapeLike(term, ESCAPE_CHAR))
                .toList();

        // Special case: all terms are empty so nothing to match against
        if (escapedTerms.isEmpty()) { return null; }

        return (root, query, cb) -> {
            Path<String> path = propertyPath.asPath(root);
            Predicate[] predicates = escapedTerms.stream()
                                        .map(term -> cb.like(path, "%" + term + "%", ESCAPE_CHAR))
                                        .toArray(Predicate[]::new);
            return cb.or(predicates);
        };
    }

    /**
     * Creates a specification that matches entities where the specified date-time property falls on the
     * given {@code targetDate} (inclusive of the start of the day and exclusive of the start of the
     * following day).
     *
     * @param <T>          The entity type being queried.
     * @param propertyPath Dot-delimited property path to compare.
     * @param targetDate   The date to match.
     * @see PropertyPath#from(String)
     */
    public static <T> Specification<T> onDate(String propertyPath, LocalDate targetDate) {
    	return onDate(PropertyPath.from(propertyPath), targetDate);
    }

    /**
     * Creates a specification that matches entities where the specified date-time property falls on the
     * given {@code targetDate} (inclusive of the start of the day and exclusive of the start of the
     * following day).
     *
     * @param <T>          The entity type being queried.
     * @param propertyPath Resolved property path to compare.
     * @param targetDate   The date to match.
     */
    public static <T> Specification<T> onDate(PropertyPath propertyPath, LocalDate targetDate) {
        if (targetDate == null) { return null; }

        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

        Specification<T> afterStart = atLeast(propertyPath, start);
        Specification<T> beforeEnd = lessThan(propertyPath, end);

        return afterStart.and(beforeEnd);
    }


    /**
     * Creates a specification that matches entities where the specified property is strictly less than
     * {@code value}.
     *
     * @param <T>          The entity type being queried.
     * @param <C>          The comparable type of the property.
     * @param propertyPath Dot-delimited property path to compare.
     * @param value        Value to compare against.
     * @see PropertyPath#from(String)
     */
    public static <T, C extends Comparable<? super C>> Specification<T> lessThan(String propertyPath, C value) {
        return lessThan(PropertyPath.from(propertyPath), value);
    }

    /**
     * Creates a specification that matches entities where the specified property is strictly less than
     * {@code value}.
     *
     * @param <T>          The entity type being queried.
     * @param <C>          The comparable type of the property.
     * @param propertyPath Resolved property path to compare.
     * @param value        Value to compare against.
     */
    public static <T, C extends Comparable<? super C>> Specification<T> lessThan(PropertyPath propertyPath, C value) {
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
    public static <T, C extends Comparable<? super C>> Specification<T> greaterThan(String propertyPath, C value) {
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
    public static <T, C extends Comparable<? super C>> Specification<T> greaterThan(PropertyPath propertyPath, C value) {
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
     * @param <C>          The comparable type of the property.
     * @param propertyPath Dot-delimited property path to compare.
     * @param value        Value to compare against.
     * @see PropertyPath#from(String)
     */
    public static <T, C extends Comparable<? super C>> Specification<T> atLeast(String propertyPath, C value) {
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
    public static <T, C extends Comparable<? super C>> Specification<T> atLeast(PropertyPath propertyPath, C value) {
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
    public static <T, C extends Comparable<? super C>> Specification<T> between(String propertyPath, C startInclusive, C endExclusive) {
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
    public static <T, C extends Comparable<? super C>> Specification<T> between(PropertyPath propertyPath, C startInclusive, C endExclusive) {
        Specification<T> afterStart = atLeast(propertyPath, startInclusive);
        Specification<T> beforeEnd = lessThan(propertyPath, endExclusive);

        return afterStart.and(beforeEnd);
    }
}

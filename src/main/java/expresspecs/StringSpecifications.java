package expresspecs;

import static expresspecs.BasicSpecifications.unrestricted;
import static expresspecs.SQLUtils.escapeLike;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;

/**
 * Predicate factories for String-based JPA Specifications, including partial matches and case-insensitive equality.
 *
 * <p>Most of the methods return an {@linkplain BasicSpecifications#unrestricted() unrestricted specification} when the
 * filter value is {@code null}, blank, or an empty collection, making them safe to use directly from optional
 * query parameters without null-checking at the call site.
 */
@UtilityClass
public class StringSpecifications {

	static final char ESCAPE_CHAR = '\\';


	/**
	 * Creates a specification that matches entities where the specified property equals
	 * {@code aValue}, ignoring case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param aValue       Value to compare against.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> equalsIgnoreCase(String propertyPath, String aValue) {
		return equalsIgnoreCase(PropertyPath.from(propertyPath), aValue);
	}

	/**
	 * Creates a specification that matches entities where the specified property equals
	 * {@code aValue}, ignoring case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param aValue       Value to compare against.
	 */
	public static <T> @NonNull Specification<T> equalsIgnoreCase(PropertyPath propertyPath, String aValue) {
		if (aValue == null) {
			return unrestricted();
		}

		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.equal(cb.lower(path), aValue.toLowerCase(Locale.ROOT));
		};
	}

	/**
	 * Creates a specification that matches entities where the specified string property is empty:
	 * either {@code null} or an empty string ({@code ""}).
	 *
	 * <p>Useful for schemas that store missing values as either {@code null} or empty string.
	 * Produces the SQL predicate {@code (column IS NULL OR column = '')}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to a string attribute.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> isNullOrEmpty(String propertyPath) {
		return isNullOrEmpty(PropertyPath.from(propertyPath));
	}

	/**
	 * Creates a specification that matches entities where the specified string property is empty:
	 * either {@code null} or an empty string ({@code ""}).
	 *
	 * <p>Useful for schemas that store missing values as either {@code null} or empty string.
	 * Produces the SQL predicate {@code (column IS NULL OR column = '')}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to a string attribute.
	 */
	public static <T> @NonNull Specification<T> isNullOrEmpty(PropertyPath propertyPath) {
		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.or(cb.isNull(path), cb.equal(path, ""));
		};
	}

	/**
	 * Creates a specification that matches entities where the specified string property is not empty:
	 * neither {@code null} nor an empty string ({@code ""}).
	 *
	 * <p>This is the logical complement of {@link #isNullOrEmpty(String)}.
	 * Produces the SQL predicate {@code (column IS NOT NULL AND column <> '')}.
	 *
	 * <p><strong>Oracle compatibility note:</strong> This predicate does not behave correctly on Oracle.
	 * Oracle coerces {@code ''} to {@code NULL} at storage time, so the {@code column <> ''} term
	 * binds as {@code column <> NULL}, which is UNKNOWN under SQL three-valued logic. This causes
	 * every row to be excluded, even rows with real values. On Oracle, use
	 * {@link BasicSpecifications#notNull(String)} instead: since Oracle cannot store an empty string,
	 * a non-null value is guaranteed to be non-empty.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to a string attribute.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> isNotNullOrEmpty(String propertyPath) {
		return isNotNullOrEmpty(PropertyPath.from(propertyPath));
	}

	/**
	 * Creates a specification that matches entities where the specified string property is not empty:
	 * neither {@code null} nor an empty string ({@code ""}).
	 *
	 * <p>This is the logical complement of {@link #isNullOrEmpty(PropertyPath)}.
	 * Produces the SQL predicate {@code (column IS NOT NULL AND column <> '')}.
	 *
	 * <p><strong>Oracle compatibility note:</strong> This predicate does not behave correctly on Oracle.
	 * Oracle coerces {@code ''} to {@code NULL} at storage time, so the {@code column <> ''} term
	 * binds as {@code column <> NULL}, which is UNKNOWN under SQL three-valued logic. This causes
	 * every row to be excluded, even rows with real values. On Oracle, use
	 * {@link BasicSpecifications#notNull(PropertyPath)} instead: since Oracle cannot store an empty string,
	 * a non-null value is guaranteed to be non-empty.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to a string attribute.
	 */
	public static <T> @NonNull Specification<T> isNotNullOrEmpty(PropertyPath propertyPath) {
		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.and(cb.isNotNull(path), cb.notEqual(path, ""));
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property contains
	 * {@code value}. Matching is case-sensitive.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Substring to match within the property value.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> contains(String propertyPath, String value) {
		return contains(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property contains
	 * {@code value}. Matching is case-sensitive.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Substring to match within the property value.
	 */
	public static <T> @NonNull Specification<T> contains(PropertyPath propertyPath, String value) {
		if (StringUtils.isEmpty(value)) {
			return unrestricted();
		}

		String escapedValue = escapeLike(value, ESCAPE_CHAR);
		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.like(path, "%" + escapedValue + "%", ESCAPE_CHAR);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property contains
	 * {@code value}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Substring to match within the property value.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> containsIgnoreCase(String propertyPath, String value) {
		return containsIgnoreCase(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property contains
	 * {@code value}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Substring to match within the property value.
	 */
	public static <T> @NonNull Specification<T> containsIgnoreCase(PropertyPath propertyPath, String value) {
		if (StringUtils.isEmpty(value)) {
			return unrestricted();
		}

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
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Substring to exclude from the property value.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> doesNotContain(String propertyPath, String value) {
		return doesNotContain(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property does not
	 * contain {@code value}. Matching is case-sensitive.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Substring to exclude from the property value.
	 */
	public static <T> @NonNull Specification<T> doesNotContain(PropertyPath propertyPath, String value) {
		if (StringUtils.isEmpty(value)) {
			return unrestricted();
		}

		String escapedValue = escapeLike(value, ESCAPE_CHAR);
		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.notLike(path, "%" + escapedValue + "%", ESCAPE_CHAR);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property does not
	 * contain {@code value}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Substring to exclude from the property value.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> doesNotContainIgnoreCase(String propertyPath, String value) {
		return doesNotContainIgnoreCase(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property does not
	 * contain {@code value}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Substring to exclude from the property value.
	 */
	public static <T> @NonNull Specification<T> doesNotContainIgnoreCase(PropertyPath propertyPath, String value) {
		if (StringUtils.isEmpty(value)) {
			return unrestricted();
		}

		String escapedValue = escapeLike(value.toLowerCase(Locale.ROOT), ESCAPE_CHAR);
		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.notLike(cb.lower(path), "%" + escapedValue + "%", ESCAPE_CHAR);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property contains at least
	 * one of the values in {@code searchTerms}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param searchTerms  Candidate substrings for case-insensitive matching.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> containsAnyIgnoreCase(String propertyPath,
			Collection<String> searchTerms) {
		return containsAnyIgnoreCase(PropertyPath.from(propertyPath), searchTerms);
	}

	/**
	 * Creates a specification that matches entities where the specified property contains at least
	 * one of the values in {@code searchTerms}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param searchTerms  Candidate substrings for case-insensitive matching.
	 */
	public static <T> @NonNull Specification<T> containsAnyIgnoreCase(PropertyPath propertyPath,
			Collection<String> searchTerms) {
		if (CollectionUtils.isEmpty(searchTerms)) {
			return unrestricted();
		}

		List<String> escapedTerms = searchTerms.stream()
				.filter(term -> !StringUtils.isEmpty(term))
				.map(term -> escapeLike(term.toLowerCase(Locale.ROOT), ESCAPE_CHAR))
				.toList();

		// Special case: all terms are empty so nothing to match against
		if (escapedTerms.isEmpty()) {
			return unrestricted();
		}

		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			Predicate[] predicates = escapedTerms.stream()
					.map(term -> cb.like(cb.lower(path), "%" + term + "%", ESCAPE_CHAR))
					.toArray(Predicate[]::new);
			return cb.or(predicates);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property starts with
	 * {@code value}. Matching is case-sensitive.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Prefix to match at the start of the property value.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> startsWith(String propertyPath, String value) {
		return startsWith(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property starts with
	 * {@code value}. Matching is case-sensitive.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Prefix to match at the start of the property value.
	 */
	public static <T> @NonNull Specification<T> startsWith(PropertyPath propertyPath, String value) {
		if (StringUtils.isEmpty(value)) {
			return unrestricted();
		}

		String escapedValue = escapeLike(value, ESCAPE_CHAR);
		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.like(path, escapedValue + "%", ESCAPE_CHAR);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property starts with
	 * {@code value}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Prefix to match at the start of the property value.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> startsWithIgnoreCase(String propertyPath, String value) {
		return startsWithIgnoreCase(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property starts with
	 * {@code value}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Prefix to match at the start of the property value.
	 */
	public static <T> @NonNull Specification<T> startsWithIgnoreCase(PropertyPath propertyPath, String value) {
		if (StringUtils.isEmpty(value)) {
			return unrestricted();
		}

		String escapedValue = escapeLike(value.toLowerCase(Locale.ROOT), ESCAPE_CHAR);
		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.like(cb.lower(path), escapedValue + "%", ESCAPE_CHAR);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property ends with
	 * {@code value}. Matching is case-sensitive.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Suffix to match at the end of the property value.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> endsWith(String propertyPath, String value) {
		return endsWith(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property ends with
	 * {@code value}. Matching is case-sensitive.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Suffix to match at the end of the property value.
	 */
	public static <T> @NonNull Specification<T> endsWith(PropertyPath propertyPath, String value) {
		if (StringUtils.isEmpty(value)) {
			return unrestricted();
		}

		String escapedValue = escapeLike(value, ESCAPE_CHAR);
		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.like(path, "%" + escapedValue, ESCAPE_CHAR);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property ends with
	 * {@code value}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param value        Suffix to match at the end of the property value.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> endsWithIgnoreCase(String propertyPath, String value) {
		return endsWithIgnoreCase(PropertyPath.from(propertyPath), value);
	}

	/**
	 * Creates a specification that matches entities where the specified property ends with
	 * {@code value}. Matching ignores case.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param value        Suffix to match at the end of the property value.
	 */
	public static <T> @NonNull Specification<T> endsWithIgnoreCase(PropertyPath propertyPath, String value) {
		if (StringUtils.isEmpty(value)) {
			return unrestricted();
		}

		String escapedValue = escapeLike(value.toLowerCase(Locale.ROOT), ESCAPE_CHAR);
		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			return cb.like(cb.lower(path), "%" + escapedValue, ESCAPE_CHAR);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified property contains at least
	 * one of the values in {@code searchTerms}. Matching is case-sensitive.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param searchTerms  Candidate substrings for case-sensitive matching.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> containsAny(String propertyPath, Collection<String> searchTerms) {
		return containsAny(PropertyPath.from(propertyPath), searchTerms);
	}

	/**
	 * Creates a specification that matches entities where the specified property contains at least
	 * one of the values in {@code searchTerms}. Matching is case-sensitive.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param searchTerms  Candidate substrings for case-sensitive matching.
	 */
	public static <T> @NonNull Specification<T> containsAny(PropertyPath propertyPath, Collection<String> searchTerms) {
		if (CollectionUtils.isEmpty(searchTerms)) {
			return unrestricted();
		}

		List<String> escapedTerms = searchTerms.stream()
				.filter(term -> !StringUtils.isEmpty(term))
				.map(term -> escapeLike(term, ESCAPE_CHAR))
				.toList();

		// Special case: all terms are empty so nothing to match against
		if (escapedTerms.isEmpty()) {
			return unrestricted();
		}

		return (root, query, cb) -> {
			Path<String> path = propertyPath.asPath(root);
			Predicate[] predicates = escapedTerms.stream()
					.map(term -> cb.like(path, "%" + term + "%", ESCAPE_CHAR))
					.toArray(Predicate[]::new);
			return cb.or(predicates);
		};
	}

}

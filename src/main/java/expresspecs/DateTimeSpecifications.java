package expresspecs;

import static expresspecs.BasicSpecifications.unrestricted;
import static expresspecs.RangeSpecifications.atLeast;
import static expresspecs.RangeSpecifications.lessThan;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import lombok.experimental.UtilityClass;

/**
 * Predicate factories for Date/Time-based JPA Specifications.
 *
 * <p>All factory methods accept {@code null} filter values and return an {@linkplain BasicSpecifications#unrestricted()
 * unrestricted specification} in that case, making them safe to use without null-checking at the call site.
 */
@UtilityClass
public class DateTimeSpecifications {

	/**
	 * Creates a specification that matches entities where the year of the specified date-time property
	 * equals {@code year}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param year         The year to match.
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> yearIs(String propertyPath, Integer year) {
		return yearIs(PropertyPath.from(propertyPath), year);
	}

	/**
	 * Creates a specification that matches entities where the year of the specified date-time property
	 * equals {@code year}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param year         The year to match.
	 */
	public static <T> @NonNull Specification<T> yearIs(PropertyPath propertyPath, Integer year) {
		if (year == null) {
			return unrestricted();
		}
		return (root, query, cb) -> {
			Path<?> path = propertyPath.asPath(root);
			Expression<Integer> yearExpr = cb.function("year", Integer.class, path);
			return cb.equal(yearExpr, year);
		};
	}

	/**
	 * Creates a specification that matches entities where the month of the specified date-time property
	 * equals {@code month}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param month        The month to match (1–12).
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> monthIs(String propertyPath, Integer month) {
		return monthIs(PropertyPath.from(propertyPath), month);
	}

	/**
	 * Creates a specification that matches entities where the month of the specified date-time property
	 * equals {@code month}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param month        The month to match (1–12).
	 */
	public static <T> @NonNull Specification<T> monthIs(PropertyPath propertyPath, Integer month) {
		if (month == null) {
			return unrestricted();
		}
		return (root, query, cb) -> {
			Path<?> path = propertyPath.asPath(root);
			Expression<Integer> monthExpr = cb.function("month", Integer.class, path);
			return cb.equal(monthExpr, month);
		};
	}

	/**
	 * Creates a specification that matches entities where the day of month of the specified date-time
	 * property equals {@code dayOfMonth}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Dot-delimited property path to compare.
	 * @param dayOfMonth   The day of month to match (1–31).
	 * @see PropertyPath#from(String)
	 */
	public static <T> @NonNull Specification<T> dayOfMonthIs(String propertyPath, Integer dayOfMonth) {
		return dayOfMonthIs(PropertyPath.from(propertyPath), dayOfMonth);
	}

	/**
	 * Creates a specification that matches entities where the day of month of the specified date-time
	 * property equals {@code dayOfMonth}.
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param dayOfMonth   The day of month to match (1–31).
	 */
	public static <T> @NonNull Specification<T> dayOfMonthIs(PropertyPath propertyPath, Integer dayOfMonth) {
		if (dayOfMonth == null) {
			return unrestricted();
		}
		return (root, query, cb) -> {
			Path<?> path = propertyPath.asPath(root);
			Expression<Integer> dayExpr = cb.function("day", Integer.class, path);
			return cb.equal(dayExpr, dayOfMonth);
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
	public static <T> @NonNull Specification<T> onDate(String propertyPath, LocalDate targetDate) {
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
	public static <T> @NonNull Specification<T> onDate(PropertyPath propertyPath, LocalDate targetDate) {
		if (targetDate == null) {
			return unrestricted();
		}

		LocalDateTime start = targetDate.atStartOfDay();
		LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

		Specification<T> afterStart = atLeast(propertyPath, start);
		Specification<T> beforeEnd = lessThan(propertyPath, end);

		return afterStart.and(beforeEnd);
	}

}

package rizzoweb.spring.jpa.specifications;

import static rizzoweb.spring.jpa.specifications.BasicSpecifications.unrestricted;
import static rizzoweb.spring.jpa.specifications.RangeSpecifications.atLeast;
import static rizzoweb.spring.jpa.specifications.RangeSpecifications.lessThan;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import lombok.experimental.UtilityClass;

/**
 * Predicate factories for Date/Time-based JPA Specifications.
 */
@UtilityClass
public class DateTimeSpecifications {

	// TODO: Add Date/Time Part Extractors (yearIs, monthIs, dayOfMonthIs)

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

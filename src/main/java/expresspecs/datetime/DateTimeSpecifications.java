package expresspecs.datetime;

import static expresspecs.BasicSpecifications.unrestricted;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import expresspecs.PropertyPath;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import lombok.experimental.UtilityClass;

/**
 * Predicate factories for Date/Time-based JPA Specifications.
 *
 * <p>All factory methods accept {@code null} filter values and return an {@linkplain expresspecs.BasicSpecifications#unrestricted()
 * unrestricted specification} in that case, making them safe to use without null-checking at the call site.
 *
 * <p><strong>Hibernate requirement:</strong> Some methods (such as {@link #yearIs}, {@link #monthIs},
 * and {@link #dayOfMonthIs}) use the Hibernate-specific {@code HibernateCriteriaBuilder} API to
 * generate portable {@code EXTRACT}-based SQL that works across all supported databases. These methods
 * will throw {@link ClassCastException} at runtime if the JPA provider is not Hibernate. All other
 * methods in this class rely only on the standard JPA Criteria API and are provider-neutral.
 */
@UtilityClass
public class DateTimeSpecifications {

	private HibernateCriteriaBuilder hibernateBuilder(CriteriaBuilder builder) {
		return (HibernateCriteriaBuilder) builder;
	}

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
			Expression<Integer> yearExpr = hibernateBuilder(cb).year(propertyPath.asPath(root));
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
			Expression<Integer> monthExpr = hibernateBuilder(cb).month(propertyPath.asPath(root));
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
			Expression<Integer> dayExpr = hibernateBuilder(cb).day(propertyPath.asPath(root));
			return cb.equal(dayExpr, dayOfMonth);
		};
	}

	/**
	 * Creates a specification that matches entities where the specified temporal property falls on the
	 * given {@code targetDate}. How {@code targetDate} is interpreted depends on the leaf property type
	 * (see {@link #onDate(PropertyPath, LocalDate)}).
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
	 * Creates a specification that matches entities where the specified temporal property falls on the
	 * given calendar {@code targetDate}.
	 *
	 * <p>Predicate construction walks a fixed-order list of {@link SameCalendarDay} implementations until one
	 * {@linkplain SameCalendarDay#supports(Class) supports} the leaf property type.
	 *
	 * <p><strong>Semantics by leaf property type</strong>
	 *
	 * <ul>
	 *   <li>{@link Instant}, {@link OffsetDateTime}, {@link ZonedDateTime}, {@link Date}, and
	 *   {@link java.sql.Timestamp}: values are treated as instants on the UTC timeline. The range is
	 *   {@code [targetDate at 00:00 UTC, targetDate.plusDays(1) at 00:00 UTC)} (half-open).</li>
	 *   <li>{@link LocalDate}: {@linkplain jakarta.persistence.criteria.CriteriaBuilder#equal equal}
	 *   to {@code targetDate}.</li>
	 *   <li>{@link java.sql.Date}: equal to {@link java.sql.Date#valueOf(LocalDate)} for {@code targetDate}.</li>
	 *   <li>{@link LocalDateTime}: half-open range using {@linkplain LocalDate#atStartOfDay() start of day}
	 *   through the following midnight in the <em>same</em> {@link LocalDateTime} calendar (no zone
	 *   conversion).</li>
	 *   <li>Any other leaf property type: {@link UnsupportedDatePropertyException} (a subtype of
	 *   {@link IllegalArgumentException}) when the specification is evaluated, because {@code onDate}
	 *   does not define calendar-day semantics for that type.</li>
	 * </ul>
	 *
	 * @param <T>          The entity type being queried.
	 * @param propertyPath Resolved property path to compare.
	 * @param targetDate   The date to match.
	 * @throws UnsupportedDatePropertyException if the leaf property type is not one of the supported temporal types
	 * (see list in this method's description). When the specification is run through Spring Data JPA,
	 * this exception may be wrapped in a {@link org.springframework.dao.DataAccessException}.
	 */
	public static <T> @NonNull Specification<T> onDate(PropertyPath propertyPath, LocalDate targetDate) {
		if (targetDate == null) {
			return unrestricted();
		}

		return new OnDateSpecification<>(propertyPath, targetDate);
	}

}

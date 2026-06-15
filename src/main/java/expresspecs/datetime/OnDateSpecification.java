package expresspecs.datetime;

import java.time.LocalDate;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import expresspecs.PropertyPath;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

/**
 * {@link Specification} that matches entities whose temporal property falls on a given calendar
 * {@link LocalDate}.
 *
 * <p>When the specification is translated to a {@linkplain jakarta.persistence.criteria.Predicate predicate},
 * the property path's Java type selects the first {@link SameCalendarDay} implementation that
 * {@linkplain SameCalendarDay#supports(Class) supports} that type. Unsupported types yield
 * {@link UnsupportedDatePropertyException} at evaluation time.
 *
 * <p>{@link DateTimeSpecifications#onDate(PropertyPath, LocalDate)} is the typical entry point: it returns
 * an instance of this class for a non-null date, and {@link expresspecs.BasicSpecifications#unrestricted()}
 * when the date is {@code null}. The detailed semantics per supported property type are documented on that method.
 *
 * @param <T> entity root type
 */
@RequiredArgsConstructor
public final class OnDateSpecification<T> implements Specification<T> {

	private static final long serialVersionUID = 1L;

	// The ordering of this list is important
	private static final List<SameCalendarDay> SAME_CALENDAR_DAY_STRATEGIES = List.of(
			new SameCalendarDayForLocalDate(),
			new SameCalendarDayForSqlDate(),
			new SameCalendarDayForInstant(),
			new SameCalendarDayForOffsetDateTime(),
			new SameCalendarDayForZonedDateTime(),
			new SameCalendarDayForLocalDateTime(),
			new SameCalendarDayForUtilDate()
			);

	private final PropertyPath propertyPath;
	private final LocalDate targetDate;


	/**
	 * @throws UnsupportedDatePropertyException if the property is of a type not supported (eg, java.util.Calendar)
	 */
	@Override
	public Predicate toPredicate(@NonNull Root<T> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder cb) {
		Path<?> path = propertyPath.asPath(root);
		Class<?> javaType = path.getJavaType();
		SameCalendarDay strategy = getSameCalendarDayStrategy(javaType);

		if (strategy == null) {
			throw new UnsupportedDatePropertyException(propertyPath, javaType, root);
		}

		return strategy.toPredicate(path, targetDate, cb);
	}

	private static SameCalendarDay getSameCalendarDayStrategy(Class<?> propertyType) {
		return SAME_CALENDAR_DAY_STRATEGIES
				.stream()
				.filter(s -> s.supports(propertyType))
				.findFirst()
				.orElse(null);
	}
}

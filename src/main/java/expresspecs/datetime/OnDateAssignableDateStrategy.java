package expresspecs.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Matches {@link Date} and JDBC subclasses (e.g. {@link java.sql.Timestamp}) via {@link Date#isAssignableFrom(Class)}.
 */
final class OnDateAssignableDateStrategy implements OnDateComparisonStrategy {

	@Override
	public boolean supports(Class<?> leafJavaType) {
		return leafJavaType != null && Date.class.isAssignableFrom(leafJavaType);
	}

	@Override
	public Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb) {
		@SuppressWarnings("unchecked")
		Path<Date> typed = (Path<Date>) path;
		Instant start = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant();
		Instant end = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
		return cb.and(
				cb.greaterThanOrEqualTo(typed, Date.from(start)),
				cb.lessThan(typed, Date.from(end)));
	}
}

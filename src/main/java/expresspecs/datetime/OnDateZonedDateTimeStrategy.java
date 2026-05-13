package expresspecs.datetime;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

final class OnDateZonedDateTimeStrategy implements OnDateComparisonStrategy {

	@Override
	public boolean supports(Class<?> leafJavaType) {
		return ZonedDateTime.class.equals(leafJavaType);
	}

	@Override
	public Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb) {
		@SuppressWarnings("unchecked")
		Path<ZonedDateTime> typed = (Path<ZonedDateTime>) path;
		ZonedDateTime start = targetDate.atStartOfDay(ZoneOffset.UTC);
		ZonedDateTime end = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC);
		return cb.and(cb.greaterThanOrEqualTo(typed, start), cb.lessThan(typed, end));
	}
}

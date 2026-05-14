package expresspecs.datetime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

final class SameCalendarDayForOffsetDateTime implements SameCalendarDay {

	@Override
	public boolean supports(Class<?> javaType) {
		return OffsetDateTime.class.equals(javaType);
	}

	@Override
	public Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb) {
		@SuppressWarnings("unchecked")
		Path<OffsetDateTime> typed = (Path<OffsetDateTime>) path;
		OffsetDateTime start = targetDate.atTime(LocalTime.MIDNIGHT).atOffset(ZoneOffset.UTC);
		OffsetDateTime end = targetDate.plusDays(1).atTime(LocalTime.MIDNIGHT).atOffset(ZoneOffset.UTC);
		return cb.and(cb.greaterThanOrEqualTo(typed, start), cb.lessThan(typed, end));
	}
}

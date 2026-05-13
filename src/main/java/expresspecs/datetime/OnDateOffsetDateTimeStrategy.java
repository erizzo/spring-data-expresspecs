package expresspecs.datetime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

final class OnDateOffsetDateTimeStrategy implements OnDateComparisonStrategy {

	@Override
	public boolean supports(Class<?> leafJavaType) {
		return OffsetDateTime.class.equals(leafJavaType);
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

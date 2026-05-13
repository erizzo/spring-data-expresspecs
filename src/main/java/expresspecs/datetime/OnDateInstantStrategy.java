package expresspecs.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

final class OnDateInstantStrategy implements OnDateComparisonStrategy {

	@Override
	public boolean supports(Class<?> leafJavaType) {
		return Instant.class.equals(leafJavaType);
	}

	@Override
	public Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb) {
		@SuppressWarnings("unchecked")
		Path<Instant> typed = (Path<Instant>) path;
		Instant start = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant();
		Instant end = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
		return cb.and(cb.greaterThanOrEqualTo(typed, start), cb.lessThan(typed, end));
	}
}

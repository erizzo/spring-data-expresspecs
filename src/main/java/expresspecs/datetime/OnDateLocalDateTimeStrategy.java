package expresspecs.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

final class OnDateLocalDateTimeStrategy implements OnDateComparisonStrategy {

	@Override
	public boolean supports(Class<?> leafJavaType) {
		return LocalDateTime.class.equals(leafJavaType);
	}

	@Override
	public Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb) {
		@SuppressWarnings("unchecked")
		Path<LocalDateTime> typed = (Path<LocalDateTime>) path;
		LocalDateTime start = targetDate.atStartOfDay();
		LocalDateTime end = targetDate.plusDays(1).atStartOfDay();
		return cb.and(cb.greaterThanOrEqualTo(typed, start), cb.lessThan(typed, end));
	}
}

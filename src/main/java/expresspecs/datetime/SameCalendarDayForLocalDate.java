package expresspecs.datetime;

import java.time.LocalDate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

final class SameCalendarDayForLocalDate implements SameCalendarDay {

	@Override
	public boolean supports(Class<?> javaType) {
		return LocalDate.class.equals(javaType);
	}

	@Override
	public Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb) {
		@SuppressWarnings("unchecked")
		Path<LocalDate> typed = (Path<LocalDate>) path;
		return cb.equal(typed, targetDate);
	}
}

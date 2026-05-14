package expresspecs.datetime;

import java.time.LocalDate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

final class SameCalendarDayForSqlDate implements SameCalendarDay {

	@Override
	public boolean supports(Class<?> javaType) {
		return java.sql.Date.class.equals(javaType);
	}

	@Override
	public Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb) {
		return cb.equal(path, java.sql.Date.valueOf(targetDate));
	}
}

package expresspecs.datetime;

import java.time.LocalDate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

final class SameCalendarDayFallback implements SameCalendarDay {

	@Override
	public boolean supports(Class<?> javaType) {
		return true;
	}

	@Override
	public Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb) {
		Class<?> javaType = path.getJavaType();
		String typeName = javaType == null ? "(unknown)" : javaType.getName();
		throw new IllegalArgumentException(
				"DateTimeSpecifications.onDate does not support leaf property type "+ typeName);
	}
}

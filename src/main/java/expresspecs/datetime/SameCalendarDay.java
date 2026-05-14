package expresspecs.datetime;

import java.time.LocalDate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * A strategy interface, implemenations of which build a JPA predicate that checks whether a temporal attribute
 * refers to the same calendar day as {@code targetDate}.
 */
public interface SameCalendarDay {

	boolean supports(Class<?> javaType);

	Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb);
}

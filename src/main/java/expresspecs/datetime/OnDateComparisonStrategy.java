package expresspecs.datetime;

import java.time.LocalDate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Builds an {@code onDate} predicate for a single leaf temporal type. Implementations are composed in
 * order by {@link DefaultOnDateStrategyChain}; the first strategy whose {@link #supports(Class)}
 * returns {@code true} for {@link Path#getJavaType()} is used.
 */
public interface OnDateComparisonStrategy {

	/**
	 * @param leafJavaType {@link Path#getJavaType()} for the leaf path, possibly {@code null}
	 */
	boolean supports(Class<?> leafJavaType);

	Predicate toPredicate(Path<?> path, LocalDate targetDate, CriteriaBuilder cb);
}

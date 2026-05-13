package expresspecs.datetime;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Applies {@link OnDateComparisonStrategy} instances in declaration order until one {@linkplain
 * OnDateComparisonStrategy#supports(Class) supports} the leaf Java type. Intended to be constructed with a
 * fixed list (for example {@link OnDateBuiltinStrategies#defaultOrderedStrategies()}) so the same
 * constructor can later be satisfied by Spring {@code List<OnDateComparisonStrategy>} injection.
 */
public final class DefaultOnDateStrategyChain {

	private final List<OnDateComparisonStrategy> strategies;

	public DefaultOnDateStrategyChain(List<OnDateComparisonStrategy> strategies) {
		if (strategies.isEmpty()) {
			throw new IllegalArgumentException("strategies must not be empty");
		}
		this.strategies = List.copyOf(strategies);
	}

	/**
	 * Chain backed solely by {@linkplain OnDateBuiltinStrategies built-in} strategies (including a
	 * terminal fallback).
	 */
	public static DefaultOnDateStrategyChain defaults() {
		return new DefaultOnDateStrategyChain(OnDateBuiltinStrategies.defaultOrderedStrategies());
	}

	public Predicate build(Path<?> path, LocalDate targetDate, CriteriaBuilder cb) {
		Objects.requireNonNull(path, "path");
		Objects.requireNonNull(targetDate, "targetDate");
		Objects.requireNonNull(cb, "cb");
		Class<?> javaType = path.getJavaType();
		for (OnDateComparisonStrategy strategy : strategies) {
			if (strategy.supports(javaType)) {
				return strategy.toPredicate(path, targetDate, cb);
			}
		}
		throw new AssertionError("unreachable: fallback strategy must support any leaf type");
	}
}

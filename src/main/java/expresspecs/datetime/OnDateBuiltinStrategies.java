package expresspecs.datetime;

import java.util.List;

/**
 * Built-in {@link OnDateComparisonStrategy} implementations and their default order (terminal fallback last).
 */
public final class OnDateBuiltinStrategies {

	private OnDateBuiltinStrategies() {
	}

	/**
	 * Ordered list suitable for {@link DefaultOnDateStrategyChain#DefaultOnDateStrategyChain(List)}.
	 * The last element matches any leaf type not handled earlier.
	 */
	public static List<OnDateComparisonStrategy> defaultOrderedStrategies() {
		return List.of(
				new OnDateLocalDateStrategy(),
				new OnDateSqlDateStrategy(),
				new OnDateInstantStrategy(),
				new OnDateOffsetDateTimeStrategy(),
				new OnDateZonedDateTimeStrategy(),
				new OnDateLocalDateTimeStrategy(),
				new OnDateAssignableDateStrategy(),
				new OnDateFallbackStrategy());
	}
}

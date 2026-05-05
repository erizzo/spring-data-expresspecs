package expresspecs;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SQLUtils {

	/**
	 * Escapes SQL LIKE special characters (% and _) and the escapeChar itself.
	 */
	public static String escapeLike(String value, char escapeChar) {
		if (value == null) {
			return null;
		}

		var escapeString = String.valueOf(escapeChar);
		return value.replace(escapeString, escapeString + escapeString)
					.replace("_", escapeChar + "_")
					.replace("%", escapeChar + "%");
	}

}

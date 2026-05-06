package expresspecs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SQLUtilsTests {

	@Test
	void escapeLike_NullInput_ReturnsNull() {
		assertThat(SQLUtils.escapeLike(null, '\\')).isNull();
	}

	@Test
	void escapeLike_NoSpecialChars_ReturnsUnchanged() {
		assertThat(SQLUtils.escapeLike("hello world", '\\')).isEqualTo("hello world");
	}

	@Test
	void escapeLike_EmptyString_ReturnsEmpty() {
		assertThat(SQLUtils.escapeLike("", '\\')).isEmpty();
	}

	@Test
	void escapeLike_Percent_IsEscaped() {
		assertThat(SQLUtils.escapeLike("50% off", '\\')).isEqualTo("50\\% off");
	}

	@Test
	void escapeLike_Underscore_IsEscaped() {
		assertThat(SQLUtils.escapeLike("first_name", '\\')).isEqualTo("first\\_name");
	}

	@Test
	void escapeLike_EscapeChar_IsEscaped() {
		assertThat(SQLUtils.escapeLike("a\\b", '\\')).isEqualTo("a\\\\b");
	}

	@Test
	void escapeLike_MultipleSpecialChars_AllEscaped() {
		assertThat(SQLUtils.escapeLike("50%_off", '\\')).isEqualTo("50\\%\\_off");
	}

	@Test
	void escapeLike_EscapeCharAdjacentToSpecialChar_EscapedInOrder() {
		// backslash followed by percent: backslash must be escaped first, then percent
		assertThat(SQLUtils.escapeLike("a\\%b", '\\')).isEqualTo("a\\\\\\%b");
	}

	@Test
	void escapeLike_CustomEscapeChar_UsesCorrectChar() {
		assertThat(SQLUtils.escapeLike("50% off_deals", '!')).isEqualTo("50!% off!_deals");
	}

	@Test
	void escapeLike_CustomEscapeCharInInput_IsEscaped() {
		assertThat(SQLUtils.escapeLike("a!b", '!')).isEqualTo("a!!b");
	}

}

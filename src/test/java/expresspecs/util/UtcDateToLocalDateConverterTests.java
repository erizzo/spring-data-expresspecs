package expresspecs.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Test;

class UtcDateToLocalDateConverterTests {

	private final UtcDateToLocalDateConverter converter = new UtcDateToLocalDateConverter();

	@Test
	void convertToDatabaseColumn_Null_ReturnsNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void convertToEntityAttribute_Null_ReturnsNull() {
		assertThat(converter.convertToEntityAttribute(null)).isNull();
	}

	@Test
	void convertToDatabaseColumn_UsesUtcCalendarDate() {
		Date lateInUtcDay = Date.from(Instant.parse("2007-12-03T23:30:00Z"));
		Date earlyInUtcDay = Date.from(Instant.parse("2007-12-03T00:30:00Z"));

		assertThat(converter.convertToDatabaseColumn(lateInUtcDay)).isEqualTo(LocalDate.of(2007, 12, 3));
		assertThat(converter.convertToDatabaseColumn(earlyInUtcDay)).isEqualTo(LocalDate.of(2007, 12, 3));
	}

	@Test
	void convertToDatabaseColumn_SqlDate() {
		Date sqlDate = new java.sql.Date(Instant.parse("2007-12-03T12:00:00Z").toEpochMilli());

		assertThat(converter.convertToDatabaseColumn(sqlDate)).isEqualTo(LocalDate.of(2007, 12, 3));
	}

	@Test
	void convertToEntityAttribute_ReturnsUtcMidnight() {
		Date result = converter.convertToEntityAttribute(LocalDate.of(2007, 12, 3));

		assertThat(result.toInstant()).isEqualTo(Instant.parse("2007-12-03T00:00:00Z"));
	}

	@Test
	void roundTrip_PreservesDate() {
		LocalDate date = LocalDate.of(2007, 12, 3);

		assertThat(converter.convertToDatabaseColumn(converter.convertToEntityAttribute(date))).isEqualTo(date);
	}
}

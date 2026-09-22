package expresspecs.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Maps a {@link Date} entity attribute to a date-only ({@link LocalDate}) column, taking the calendar
 * date in UTC.
 *
 * <p>Use it in place of the deprecated {@code @Temporal(TemporalType.DATE)} when a date-only column must
 * stay typed as {@link Date} in the entity:
 *
 * <pre>{@code
 * @Convert(converter = UtcDateToLocalDateConverter.class)
 * private Date birthDate;
 * }</pre>
 *
 * <p>Because the conversion uses UTC, stored dates agree with the UTC calendar days that
 * {@link expresspecs.datetime.DateTimeSpecifications#onDate(expresspecs.PropertyPath, LocalDate) onDate}
 * uses for {@link Date} properties, regardless of the JVM time zone or Hibernate version. Values are
 * loaded as midnight UTC. See the usage guide section "{@code java.util.Date} date-only columns".
 */
@Converter
public class UtcDateToLocalDateConverter implements AttributeConverter<Date, LocalDate> {

	@Override
	public LocalDate convertToDatabaseColumn(Date date) {
		return date == null ? null : LocalDate.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneOffset.UTC);
	}

	@Override
	public Date convertToEntityAttribute(LocalDate date) {
		return date == null ? null : Date.from(date.atStartOfDay(ZoneOffset.UTC).toInstant());
	}
}

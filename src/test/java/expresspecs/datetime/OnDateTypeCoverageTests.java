package expresspecs.datetime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import expresspecs.example.Customer;
import expresspecs.example.Customer.Fields;
import rizzoweb.spring.jpa.DataIntegrationTest;

/**
 * Integration tests that exercise {@link DateTimeSpecifications#onDate} against common temporal
 * field types ({@code LocalDate}, {@code LocalDateTime}, {@code Instant}, legacy {@code Date},
 * {@code OffsetDateTime}, {@code ZonedDateTime}) using {@link Socialite} and {@link Customer} persistence fixtures.
 */
@Transactional
public interface OnDateTypeCoverageTests extends DataIntegrationTest<Customer> {

	JpaSpecificationExecutor<Socialite> getSocialiteRepo();

	@Test
	default void onDate_LocalDateField() {
		var dec3 = persistAndFlush(Socialite.builder()
				.localDate(LocalDate.of(2007, 12, 3))
				.build());

		persistAndFlush(Socialite.builder()
				.localDate(LocalDate.of(2007, 12, 4))
				.build());

		Specification<Socialite> spec = DateTimeSpecifications.onDate(Socialite.Fields.localDate, LocalDate.of(2007, 12, 3));
		List<Socialite> results = getSocialiteRepo().findAll(spec);

		assertThat(results).containsExactly(dec3);
	}

	@Test
	default void onDate_LocalDateTimeField() {
		var dec3 = persistAndFlush(Socialite.builder()
				.localDateTime(LocalDateTime.parse("2007-12-03T10:15:30"))
				.build());

		persistAndFlush(Socialite.builder()
				.localDateTime(LocalDateTime.parse("2007-12-04T10:15:30"))
				.build());

		Specification<Socialite> spec = DateTimeSpecifications.onDate(Socialite.Fields.localDateTime, LocalDate.of(2007, 12, 3));
		List<Socialite> results = getSocialiteRepo().findAll(spec);

		assertThat(results).containsExactly(dec3);
	}

	/**
	 * {@code LocalDateTime} uses wall-clock midnight bounds, not UTC. A value at the start of the
	 * next calendar day must not match the previous {@code onDate} target.
	 */
	@Test
	default void onDate_LocalDateTimeField_midnightBoundary() {
		var clearlyDec3 = persistAndFlush(Socialite.builder()
				.localDateTime(LocalDateTime.parse("2007-12-03T23:45"))
				.build());

		persistAndFlush(Socialite.builder()
				.localDateTime(LocalDateTime.parse("2007-12-04T00:00"))
				.build());

		Specification<Socialite> spec = DateTimeSpecifications.onDate(Socialite.Fields.localDateTime, LocalDate.of(2007, 12, 3));
		List<Socialite> results = getSocialiteRepo().findAll(spec);

		assertThat(results).containsExactly(clearlyDec3);
	}

	@Test
	default void onDate_InstantField() {
		var dec3 = persistAndFlush(Customer.builder()
				.instantTimestamp(Instant.parse("2007-12-03T10:15:30Z"))
				.build());

		persistAndFlush(Customer.builder()
				.instantTimestamp(Instant.parse("2007-12-04T10:15:30Z"))
				.build());

		Specification<Customer> spec = DateTimeSpecifications.onDate(Fields.instantTimestamp, LocalDate.of(2007, 12, 3));
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(dec3);
	}

	@Test
	default void onDate_JavaUtilDateAsTimestampField() {
		var dec3 = persistAndFlush(Socialite.builder()
				.javaUtilDateAsTimestamp(Date.from(Instant.parse("2007-12-03T10:15:30Z")))
				.build());

		persistAndFlush(Socialite.builder()
				.javaUtilDateAsTimestamp(Date.from(Instant.parse("2007-12-04T10:15:30Z")))
				.build());

		Specification<Socialite> spec = DateTimeSpecifications.onDate(Socialite.Fields.javaUtilDateAsTimestamp, LocalDate.of(2007, 12, 3));
		List<Socialite> results = getSocialiteRepo().findAll(spec);

		assertThat(results).containsExactly(dec3);
	}

	@Test
	default void onDate_SqlDateField() {
		var dec3 = persistAndFlush(Socialite.builder()
				.sqlDate(java.sql.Date.valueOf("2007-12-03"))
				.build());

		persistAndFlush(Socialite.builder()
				.sqlDate(java.sql.Date.valueOf("2007-12-04"))
				.build());

		Specification<Socialite> spec = DateTimeSpecifications.onDate(Socialite.Fields.sqlDate, LocalDate.of(2007, 12, 3));
		List<Socialite> results = getSocialiteRepo().findAll(spec);

		assertThat(results).containsExactly(dec3);
	}

	@Test
	default void onDate_JavaUtilDateAsDateField() {
		var dec3 = persistAndFlush(Socialite.builder()
				.javaUtilDateAsDate(Date.from(Instant.parse("2007-12-03T10:15:30Z")))
				.build());

		persistAndFlush(Socialite.builder()
				.javaUtilDateAsDate(Date.from(Instant.parse("2007-12-04T10:15:30Z")))
				.build());

		Specification<Socialite> spec = DateTimeSpecifications.onDate(Socialite.Fields.javaUtilDateAsDate, LocalDate.of(2007, 12, 3));
		List<Socialite> results = getSocialiteRepo().findAll(spec);

		assertThat(results).containsExactly(dec3);
	}

	@Test
	default void onDate_SqlTimestampField() {
		var dec3 = persistAndFlush(Socialite.builder()
				.sqlTimestamp(Timestamp.from(Instant.parse("2007-12-03T10:15:30Z")))
				.build());

		persistAndFlush(Socialite.builder()
				.sqlTimestamp(Timestamp.from(Instant.parse("2007-12-04T10:15:30Z")))
				.build());

		Specification<Socialite> spec = DateTimeSpecifications.onDate(Socialite.Fields.sqlTimestamp, LocalDate.of(2007, 12, 3));
		List<Socialite> results = getSocialiteRepo().findAll(spec);

		assertThat(results).containsExactly(dec3);
	}

	@Test
	default void onDate_Fallback_throwsIllegalArgumentException() {
		Calendar dec3 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		dec3.set(2007, Calendar.DECEMBER, 3, 10, 15, 30);
		dec3.set(Calendar.MILLISECOND, 0);
		persistAndFlush(Socialite.builder()
				.javaUtilCalendar(dec3)
				.build());

		Specification<Socialite> spec = DateTimeSpecifications.onDate(Socialite.Fields.javaUtilCalendar, LocalDate.of(2007, 12, 3));

		assertThatThrownBy(() -> getSocialiteRepo().findAll(spec))
				.isInstanceOf(DataAccessException.class)
				.hasCauseInstanceOf(IllegalArgumentException.class)
				.cause()
				.hasMessageContaining("java.util.Calendar")
				.hasMessageContaining("DateTimeSpecifications.onDate");
	}

	/**
	 * Stores a timestamp that is Dec 3 in local time but Dec 4 in UTC
	 * ({@code 2007-12-03T23:30:00-01:00 == 2007-12-04T00:30:00Z}), alongside a value
	 * that is unambiguously Dec 3 UTC. A query for Dec 3 should return only the latter.
	 */
	@Test
	default void onDate_OffsetDateTimeField_offsetCrossesMidnight() {
		var clearlyDec3 = persistAndFlush(Customer.builder()
				.createdTimestamp(OffsetDateTime.parse("2007-12-03T10:00:00+00:00"))
				.build());

		persistAndFlush(Customer.builder()
				.createdTimestamp(OffsetDateTime.parse("2007-12-03T23:30:00-01:00"))
				.build());

		Specification<Customer> spec = DateTimeSpecifications.onDate(Fields.createdTimestamp, LocalDate.of(2007, 12, 3));
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(clearlyDec3);
	}

	/**
	 * Same UTC-boundary behavior as {@link #onDate_OffsetDateTimeField_offsetCrossesMidnight()},
	 * for {@link ZonedDateTime} columns.
	 */
	@Test
	default void onDate_ZonedDateTimeField_offsetCrossesMidnight() {
		var clearlyDec3 = persistAndFlush(Socialite.builder()
				.zonedDateTime(ZonedDateTime.parse("2007-12-03T10:00:00+00:00"))
				.build());

		persistAndFlush(Socialite.builder()
				.zonedDateTime(ZonedDateTime.parse("2007-12-03T23:30:00-01:00"))
				.build());

		Specification<Socialite> spec = DateTimeSpecifications.onDate(Socialite.Fields.zonedDateTime, LocalDate.of(2007, 12, 3));
		List<Socialite> results = getSocialiteRepo().findAll(spec);

		assertThat(results).containsExactly(clearlyDec3);
	}
}

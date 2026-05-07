package expresspecs;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import expresspecs.example.Customer;
import expresspecs.example.Customer.Fields;
import rizzoweb.spring.jpa.DataIntegrationTest;

/**
 * Integration tests that exercise {@link DateTimeSpecifications#onDate} against every common
 * temporal field type. Several of these are expected to fail with the current implementation,
 * demonstrating that {@code onDate} only works correctly for {@code LocalDateTime} fields today.
 */
@Transactional
public interface OnDateTypeCoverageTests extends DataIntegrationTest<Customer> {

	@Test
	default void onDate_LocalDateField() {
		var dec3 = persistAndFlush(Customer.builder()
				.localDateOnly(LocalDate.of(2007, 12, 3))
				.build());

		persistAndFlush(Customer.builder()
				.localDateOnly(LocalDate.of(2007, 12, 4))
				.build());

		Specification<Customer> spec = DateTimeSpecifications.onDate(Fields.localDateOnly, LocalDate.of(2007, 12, 3));
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(dec3);
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
	default void onDate_LegacyDateField() {
		var dec3 = persistAndFlush(Customer.builder()
				.legacyDate(Date.from(Instant.parse("2007-12-03T10:15:30Z")))
				.build());

		persistAndFlush(Customer.builder()
				.legacyDate(Date.from(Instant.parse("2007-12-04T10:15:30Z")))
				.build());

		Specification<Customer> spec = DateTimeSpecifications.onDate(Fields.legacyDate, LocalDate.of(2007, 12, 3));
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(dec3);
	}

	/**
	 * Stores a timestamp that is Dec 3 in local time but Dec 4 in UTC
	 * ({@code 2007-12-03T23:30:00-01:00 == 2007-12-04T00:30:00Z}), alongside a value
	 * that is unambiguously Dec 3 UTC. A query for Dec 3 should return only the latter.
	 *
	 * <p>The current implementation passes {@code LocalDateTime} bounds to JPA regardless
	 * of the field type. For an {@code OffsetDateTime} column, some databases strip the
	 * offset and compare using the local time, producing a false positive for the
	 * {@code -01:00} record.
	 */
	@Test
	default void onDate_OffsetDateTimeField_offsetCrossesMidnight() {
		var clearlyDec3 = persistAndFlush(Customer.builder()
				.createdTimestamp(OffsetDateTime.parse("2007-12-03T10:00:00+00:00"))
				.build());

		// Local time is Dec 3, but UTC equivalent is Dec 4 at 00:30 — must NOT appear in a Dec-3 query
		persistAndFlush(Customer.builder()
				.createdTimestamp(OffsetDateTime.parse("2007-12-03T23:30:00-01:00"))
				.build());

		Specification<Customer> spec = DateTimeSpecifications.onDate(Fields.createdTimestamp, LocalDate.of(2007, 12, 3));
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(clearlyDec3);
	}
}

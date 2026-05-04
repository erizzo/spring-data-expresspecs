package rizzoweb.spring.jpa.specifications;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import rizzoweb.spring.jpa.DataIntegrationTest;
import rizzoweb.spring.jpa.specifications.example.Customer;
import rizzoweb.spring.jpa.specifications.example.Customer.Fields;

@Transactional
public interface DateTimeSpecificationIntegrationTests extends DataIntegrationTest {

	@Test
	default void onDate() {
		var newbie = Customer.builder()
						.createdTimestamp(OffsetDateTime.now().minusMonths(1))
						.build();
		newbie = persistAndFlush(newbie);

		var oldSchool = Customer.builder()
						.createdTimestamp(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"))
						.build();
		oldSchool = persistAndFlush(oldSchool);

		Specification<Customer> spec = DateTimeSpecifications.onDate(Customer.Fields.createdTimestamp, LocalDate.parse("2007-12-03"));
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(oldSchool);
	}

	@Test
	default void yearIs() {
		var y2007 = Customer.builder()
						.createdTimestamp(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"))
						.build();
		y2007 = persistAndFlush(y2007);

		var y2024 = Customer.builder()
						.createdTimestamp(OffsetDateTime.parse("2024-06-15T08:00:00+00:00"))
						.build();
		y2024 = persistAndFlush(y2024);

		Specification<Customer> spec = DateTimeSpecifications.yearIs(Fields.createdTimestamp, 2007);
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(y2007);
	}

	@Test
	default void monthIs() {
		var december = Customer.builder()
						.createdTimestamp(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"))
						.build();
		december = persistAndFlush(december);

		var march = Customer.builder()
						.createdTimestamp(OffsetDateTime.parse("2007-03-15T10:15:30+01:00"))
						.build();
		march = persistAndFlush(march);

		Specification<Customer> spec = DateTimeSpecifications.monthIs(Fields.createdTimestamp, 12);
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(december);
	}

	@Test
	default void dayOfMonthIs() {
		var theThird = Customer.builder()
						.createdTimestamp(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"))
						.build();
		theThird = persistAndFlush(theThird);

		var theXmas = Customer.builder()
						.createdTimestamp(OffsetDateTime.parse("2007-12-25T10:15:30+01:00"))
						.build();
		theXmas = persistAndFlush(theXmas);

		Specification<Customer> spec = DateTimeSpecifications.dayOfMonthIs(Fields.createdTimestamp, 3);
		List<Customer> results = getRepo().findAll(spec);

		assertThat(results).containsExactly(theThird);
	}
}

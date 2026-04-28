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
}

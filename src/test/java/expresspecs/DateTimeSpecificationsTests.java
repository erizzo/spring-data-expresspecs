package expresspecs;

import static expresspecs.SpecificationAssert.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class DateTimeSpecificationsTests {

	@Test
	void yearIs_NullYear() {
		Specification<Object> result = DateTimeSpecifications.yearIs("foo", null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void yearIs_NestedPath_NullYear() {
		PropertyPath path = PropertyPath.of("foo", "bar");
		Specification<Object> result = DateTimeSpecifications.yearIs(path, null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void monthIs_NullMonth() {
		Specification<Object> result = DateTimeSpecifications.monthIs("foo", null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void monthIs_NestedPath_NullMonth() {
		PropertyPath path = PropertyPath.of("foo", "bar");
		Specification<Object> result = DateTimeSpecifications.monthIs(path, null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void dayOfMonthIs_NullDayOfMonth() {
		Specification<Object> result = DateTimeSpecifications.dayOfMonthIs("foo", null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void dayOfMonthIs_NestedPath_NullDayOfMonth() {
		PropertyPath path = PropertyPath.of("foo", "bar");
		Specification<Object> result = DateTimeSpecifications.dayOfMonthIs(path, null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void onDate_NullDate() {
		Specification<Object> result = DateTimeSpecifications.onDate("foo", (LocalDate) null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void onDate_NestedPath_NullDate() {
		PropertyPath path = PropertyPath.of("foo", "bar");
		Specification<Object> result = DateTimeSpecifications.onDate(path, (LocalDate) null);
		assertThat(result).isUnrestricted();
	}

}

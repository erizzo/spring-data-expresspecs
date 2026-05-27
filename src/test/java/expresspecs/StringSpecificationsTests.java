package expresspecs;

import static expresspecs.SpecificationAssert.assertThat;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.data.jpa.domain.Specification;

class StringSpecificationsTests {

	@Test
	void equalsIgnoreCase_NullValue_ReturnsUnrestricted() {
		Specification<Object> result = StringSpecifications.equalsIgnoreCase("foo", null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void equalsIgnoreCase_NestedProperty_NullValue_ReturnsUnrestricted() {
		Specification<Object> result = StringSpecifications.equalsIgnoreCase(PropertyPath.of("foo", "bar"), null);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void containsIgnoreCase_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.containsIgnoreCase("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void containsIgnoreCase_NestedProperty_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.containsIgnoreCase(PropertyPath.of("foo", "bar"), emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void doesNotContain_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.doesNotContain("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void doesNotContain_NestedProperty_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.doesNotContain(PropertyPath.of("foo", "bar"), emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void doesNotContainIgnoreCase_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.doesNotContainIgnoreCase("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void doesNotContainIgnoreCase_NestedProperty_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.doesNotContainIgnoreCase(PropertyPath.of("foo", "bar"), emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@Test
	void containsAnyIgnoreCase_NullList() {
		Specification<Object> result = StringSpecifications.containsAnyIgnoreCase("foo", null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void containsAnyIgnoreCase_EmptyList() {
		Specification<Object> result = StringSpecifications.containsAnyIgnoreCase("foo", emptyList());
		assertThat(result).isUnrestricted();
	}

	@Test
	void containsAnyIgnoreCase_AllEmptyTerms() {
		List<String> terms = new ArrayList<>();
		terms.add("");
		terms.add(null);

		Specification<Object> result = StringSpecifications.containsAnyIgnoreCase("foo", terms);
		assertThat(result).isUnrestricted();
	}


	@ParameterizedTest
	@NullSource
	@EmptySource
	void contains_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.contains("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void contains_NestedProperty_EmptySearchValue(String emptySearchValue) {
		var path = PropertyPath.of("foo", "bar");
		Specification<Object> result = StringSpecifications.contains(path , emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@Test
	void containsAny_NullList() {
		Specification<Object> result = StringSpecifications.containsAny("foo", null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void containsAny_EmptyList() {
		Specification<Object> result = StringSpecifications.containsAny("foo", emptyList());
		assertThat(result).isUnrestricted();
	}

	@Test
	void containsAny_AllEmptyTerms() {
		List<String> terms = new ArrayList<>();
			terms.add("");
			terms.add(null);

		Specification<Object> result = StringSpecifications.containsAny("foo", terms);
		assertThat(result).isUnrestricted();
	}

	@Test
	void containsAny_AllBlankTerms() {
		var terms = List.of("", "	", "\t");
		Specification<Object> result = StringSpecifications.containsAny("foo", terms);
		assertThat(result).isNotNull();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void startsWith_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.startsWith("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void startsWith_NestedProperty_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.startsWith(PropertyPath.of("foo", "bar"), emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void startsWithIgnoreCase_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.startsWithIgnoreCase("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void startsWithIgnoreCase_NestedProperty_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.startsWithIgnoreCase(PropertyPath.of("foo", "bar"), emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void endsWith_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.endsWith("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void endsWith_NestedProperty_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.endsWith(PropertyPath.of("foo", "bar"), emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void endsWithIgnoreCase_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.endsWithIgnoreCase("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void endsWithIgnoreCase_NestedProperty_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = StringSpecifications.endsWithIgnoreCase(PropertyPath.of("foo", "bar"), emptySearchValue);
		assertThat(result).isUnrestricted();
	}
}

package rizzoweb.spring.jpa.specifications;

import static java.util.Collections.emptyList;
import static rizzoweb.spring.jpa.specifications.SpecificationAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.data.jpa.domain.Specification;

class StringSpecificationsTests {

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
}

package expresspecs;

import static expresspecs.SpecificationAssert.assertThat;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class BasicSpecificationsTests {

	@Mock private Root<Object> root;
	@Mock private CriteriaQuery<?> query;
	@Mock private CriteriaBuilder cb;
	@Mock private Path<Object> fieldPath;


	@ParameterizedTest
	@NullSource
	@EmptySource
	void is_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = BasicSpecifications.is("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void isNot_EmptySearchValue(String emptySearchValue) {
		Specification<Object> result = BasicSpecifications.isNot("foo", emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void is_NestedPath_EmptySearchValue(String emptySearchValue) {
		PropertyPath propertyPath = PropertyPath.of("foo", "bar");
		Specification<Object> result = BasicSpecifications.is(propertyPath, emptySearchValue);
		assertThat(result).isUnrestricted();
	}

	@Test
	void isAny_EmptySearchValues() {
		List<String> searchValues = emptyList();
		Specification<Object> result = BasicSpecifications.isAny("foo", searchValues);
		assertThat(result).isUnrestricted();
	}

	@Test
	void isAny_NestedPath_EmptySearchValues() {
		List<String> searchValues = emptyList();
		var path = PropertyPath.of("foo", "bar");
		Specification<Object> result = BasicSpecifications.isAny(path , searchValues);
		assertThat(result).isUnrestricted();
	}

	@Test
	void is__ShouldConstructEqualPredicate() {
		String field = "state";
		String value = "Florida";

		when(root.get(field)).thenReturn(fieldPath);

		// Act
		Specification<Object> spec = BasicSpecifications.is(field, value);

		// Execute the lambda
		assertThat(spec).isNotNull();
		spec.toPredicate(root, query, cb);

		// Assert the methods were called during the lambda
		verify(root).get(field);
		verify(cb).equal(fieldPath, value);
	}

	@Test
	void isNotAny_EmptySearchValues() {
		List<String> searchValues = emptyList();
		Specification<Object> result = BasicSpecifications.isNotAny("foo", searchValues);
		assertThat(result).isUnrestricted();
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void isAny_ShouldConstructInPredicate() {
		String field = "status";
		List<String> values = List.of("ACTIVE", "PENDING");
		CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);

		when(root.get(field)).thenReturn(fieldPath);
		when(cb.in(fieldPath)).thenReturn(inMock);

		Specification<Object> spec = BasicSpecifications.isAny(field, values);
		spec.toPredicate(root, query, cb);

		verify(cb).in(fieldPath);
		verify(inMock).value("ACTIVE");
		verify(inMock).value("PENDING");
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void isNotAny_ShouldConstructNotInPredicate() {
		String field = "status";
		List<String> values = List.of("EXPIRED", "CANCELLED");
		CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);
		Predicate notInPredicate = mock(Predicate.class);

		when(root.get(field)).thenReturn(fieldPath);
		when(cb.in(fieldPath)).thenReturn(inMock);
		when(cb.not(inMock)).thenReturn(notInPredicate);

		Specification<Object> spec = BasicSpecifications.isNotAny(field, values);
		spec.toPredicate(root, query, cb);

		verify(cb).in(fieldPath);
		verify(inMock).value("EXPIRED");
		verify(inMock).value("CANCELLED");
		verify(cb).not(inMock);
	}

	@Test
	void unrestricted_ReturnsUnrestrictedSpecification() {
		Specification<Object> spec = BasicSpecifications.unrestricted();
		assertThat(spec).isUnrestricted();
	}

	@Test
	void where_WithEntityClass_ReturnsSameSpecification() {
		Predicate predicate = mock(Predicate.class);
		Specification<Object> original = (r, q, c) -> predicate;

		Specification<Object> result = BasicSpecifications.where(Object.class, original);
		assertThat(result.toPredicate(root, query, cb)).isSameAs(predicate);
	}

	@Test
	void where_WithEntityClass_WithUnrestricted_IsUnrestricted() {
		Specification<Object> result = BasicSpecifications.where(Object.class, BasicSpecifications.unrestricted());
		assertThat(result).isUnrestricted();
	}

	@Test
	void where_WithEntityClass_EnablesVarInference() {
		// Compile-time proof: var resolves to Specification<Object> via Class<T> pin, no type witness needed.
		var spec = BasicSpecifications.where(Object.class, BasicSpecifications.isTrue("active"))
				.and(BasicSpecifications.isTrue("verified"));
		assertThat(spec).isNotNull();
	}
}

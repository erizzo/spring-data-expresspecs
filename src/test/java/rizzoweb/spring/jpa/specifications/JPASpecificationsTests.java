package rizzoweb.spring.jpa.specifications;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static rizzoweb.spring.jpa.specifications.SpecificationAssert.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
import jakarta.persistence.criteria.Root;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class JPASpecificationsTests {

    @Mock private Root<Object> root;
    @Mock private CriteriaQuery<?> query;
    @Mock private CriteriaBuilder cb;
    @Mock private Path<Object> fieldPath;


    @ParameterizedTest
    @NullSource
    @EmptySource
    void contains_EmptySearchValue(String emptySearchValue) {
        Specification<Object> result = JPASpecifications.contains("foo", emptySearchValue);
        assertThat(result).isUnrestricted();
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void contains_NestedProperty_EmptySearchValue(String emptySearchValue) {
        var path = PropertyPath.of("foo", "bar");
		Specification<Object> result = JPASpecifications.contains(path , emptySearchValue);
        assertThat(result).isUnrestricted();
    }

    @Test
    void containsAny_NullList() {
        Specification<Object> result = JPASpecifications.containsAny("foo", null);
        assertThat(result).isUnrestricted();
    }

    @Test
    void containsAny_EmptyList() {
        Specification<Object> result = JPASpecifications.containsAny("foo", emptyList());
        assertThat(result).isUnrestricted();
    }

    @Test
    void containsAny_AllEmptyTerms() {
        List<String> terms = new ArrayList<>();
	        terms.add("");
	        terms.add(null);

        Specification<Object> result = JPASpecifications.containsAny("foo", terms);
        assertThat(result).isUnrestricted();
    }

    @Test
    void containsAny_AllBlankTerms() {
    	var terms = List.of("", "   ", "\t");
    	Specification<Object> result = JPASpecifications.containsAny("foo", terms);
    	assertThat(result).isNotNull();
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void is_EmptySearchValue(String emptySearchValue) {
        Specification<Object> result = JPASpecifications.is("foo", emptySearchValue);
        assertThat(result).isUnrestricted();
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void isNot_EmptySearchValue(String emptySearchValue) {
        Specification<Object> result = JPASpecifications.isNot("foo", emptySearchValue);
        assertThat(result).isUnrestricted();
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void is_NestedPath_EmptySearchValue(String emptySearchValue) {
      PropertyPath propertyPath = PropertyPath.of("foo", "bar");
	  Specification<Object> result = JPASpecifications.is(propertyPath, emptySearchValue);
      assertThat(result).isUnrestricted();
    }

    @Test
    void isAny_EmptySearchValues() {
    	List<String> searchValues = emptyList();
    	Specification<Object> result = JPASpecifications.isAny("foo", searchValues);
    	assertThat(result).isUnrestricted();
    }

    @Test
    void isAny_NestedPath_EmptySearchValues() {
    	List<String> searchValues = emptyList();
    	var path = PropertyPath.of("foo", "bar");
		Specification<Object> result = JPASpecifications.isAny(path , searchValues);
    	assertThat(result).isUnrestricted();
    }

    @Test
    void is__ShouldConstructEqualPredicate() {
        String field = "state";
        String value = "Florida";

        when(root.get(field)).thenReturn(fieldPath);

        // Act
        Specification<Object> spec = JPASpecifications.is(field, value);

        // Execute the lambda
        assertThat(spec).isNotNull();
        spec.toPredicate(root, query, cb);

        // Assert the methods were called during the lambda
        verify(root).get(field);
        verify(cb).equal(fieldPath, value);
    }

}

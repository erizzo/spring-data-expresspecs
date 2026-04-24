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
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void isNotEmpty_ShouldCallCbIsNotEmpty() {
        String field = "orders";
        Path collectionPath = mock(Path.class);

        when(root.get(field)).thenReturn(collectionPath);

        Specification<Object> spec = JPASpecifications.isNotEmpty(field);
        spec.toPredicate(root, query, cb);

        verify(root).get(field);
        verify(cb).isNotEmpty(collectionPath);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void isEmpty_ShouldCallCbIsEmpty() {
        String field = "orders";
        Path collectionPath = mock(Path.class);

        when(root.get(field)).thenReturn(collectionPath);

        Specification<Object> spec = JPASpecifications.isEmpty(field);
        spec.toPredicate(root, query, cb);

        verify(root).get(field);
        verify(cb).isEmpty(collectionPath);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void sizeAtLeast_ShouldCallCbSizeAndGreaterThanOrEqualTo() {
        String field = "orders";
        int minSize = 3;

        Path collectionPath = mock(Path.class);
        Expression<Integer> sizeExpr = mock(Expression.class);
        
        when(root.get(field)).thenReturn(collectionPath);
        when(cb.size(collectionPath)).thenReturn(sizeExpr);

        Specification<Object> spec = JPASpecifications.sizeAtLeast(field, minSize);
        spec.toPredicate(root, query, cb);

        verify(root).get(field);
        verify(cb).size(collectionPath);
        verify(cb).greaterThanOrEqualTo(sizeExpr, minSize);
    }

    @Test
    void isNotAny_EmptySearchValues() {
        List<String> searchValues = emptyList();
        Specification<Object> result = JPASpecifications.isNotAny("foo", searchValues);
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

        Specification<Object> spec = JPASpecifications.isAny(field, values);
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

        Specification<Object> spec = JPASpecifications.isNotAny(field, values);
        spec.toPredicate(root, query, cb);

        verify(cb).in(fieldPath);
        verify(inMock).value("EXPIRED");
        verify(inMock).value("CANCELLED");
        verify(cb).not(inMock);
    }

}


package rizzoweb.spring.jpa.specifications;

import static org.mockito.Mockito.mock;

import org.assertj.core.api.AbstractAssert;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Custom AssertJ assertion for Spring Data JPA {@link Specification}.
 */
public class SpecificationAssert<T> extends AbstractAssert<SpecificationAssert<T>, Specification<T>> {

    protected SpecificationAssert(Specification<T> actual) {
        super(actual, SpecificationAssert.class);
    }

    public static <T> SpecificationAssert<T> assertThat(Specification<T> actual) {
        return new SpecificationAssert<>(actual);
    }

    /**
     * Verifies that the specification evaluates to a null predicate (is unrestricted).
     */
    @SuppressWarnings({ "unchecked", "null" })
    public SpecificationAssert<T> isUnrestricted() {
        isNotNull();

        Root<T> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        if (actual.toPredicate(root, query, cb) != null) {
            failWithMessage("Expected specification to be unrestricted (null predicate) but it was not.");
        }
        return this;
    }
}

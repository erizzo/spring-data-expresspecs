package rizzoweb.spring.jpa.specifications;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class CollectionSpecificationsTests {

	@Mock private Root<Object> root;
	@Mock private CriteriaQuery<?> query;
	@Mock private CriteriaBuilder cb;
	@Mock private Path<Object> fieldPath;


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void isNotEmpty_ShouldCallCbIsNotEmpty() {
		String field = "orders";
		Path collectionPath = mock(Path.class);

		when(root.get(field)).thenReturn(collectionPath);

		Specification<Object> spec = CollectionSpecifications.isNotEmpty(field);
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

		Specification<Object> spec = CollectionSpecifications.isEmpty(field);
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

		Specification<Object> spec = CollectionSpecifications.sizeAtLeast(field, minSize);
		spec.toPredicate(root, query, cb);

		verify(root).get(field);
		verify(cb).size(collectionPath);
		verify(cb).greaterThanOrEqualTo(sizeExpr, minSize);
	}

	@Test
	@SuppressWarnings("unchecked")
	void containsMember_ConstructsIsMemberPredicate() {
		String field = "tags";
		String value = "VIP";
		when(root.get(field)).thenReturn(fieldPath);

		Specification<Object> spec = CollectionSpecifications.containsMember(field, value);
		spec.toPredicate(root, query, cb);

		verify(cb).isMember(eq(value), any(Path.class));
	}
}

package expresspecs;

import static expresspecs.SpecificationAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@ExtendWith(MockitoExtension.class)
class RangeSpecificationsTests {

	@Mock private Root<Object> root;
	@Mock private CriteriaQuery<?> query;
	@Mock private CriteriaBuilder cb;


	@Test
	void lessThan_NullValue_ReturnsUnrestricted() {
		Specification<Object> result = RangeSpecifications.lessThan("price", null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void lessThan_NullValue_PropertyPath_ReturnsUnrestricted() {
		Specification<Object> result = RangeSpecifications.lessThan(PropertyPath.from("price"), null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void greaterThan_NullValue_ReturnsUnrestricted() {
		Specification<Object> result = RangeSpecifications.greaterThan("price", null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void greaterThan_NullValue_PropertyPath_ReturnsUnrestricted() {
		Specification<Object> result = RangeSpecifications.greaterThan(PropertyPath.from("price"), null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void atLeast_NullValue_ReturnsUnrestricted() {
		Specification<Object> result = RangeSpecifications.atLeast("price", null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void atLeast_NullValue_PropertyPath_ReturnsUnrestricted() {
		Specification<Object> result = RangeSpecifications.atLeast(PropertyPath.from("price"), null);
		assertThat(result).isUnrestricted();
	}

	@Test
	void between_NullStart_ThrowsIllegalArgument() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> RangeSpecifications.between("price", null, 100));
	}

	@Test
	void between_NullEnd_ThrowsIllegalArgument() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> RangeSpecifications.between("price", 1, null));
	}

	@Test
	void between_NullStart_PropertyPath_ThrowsIllegalArgument() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> RangeSpecifications.between(PropertyPath.from("price"), null, 100));
	}

	@Test
	void between_NullEnd_PropertyPath_ThrowsIllegalArgument() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> RangeSpecifications.between(PropertyPath.from("price"), 1, null));
	}

}

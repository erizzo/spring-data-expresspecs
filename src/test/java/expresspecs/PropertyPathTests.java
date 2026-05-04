package expresspecs;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PropertyPathTests {

	@Nested
	@DisplayName("Factory Method: of()")
	class OfFactory {

		@Test
		@DisplayName("Should create PropertyPath from valid varargs")
		void shouldCreateFromVarargs() {
			PropertyPath path = PropertyPath.of("customer", "address", "city");

			assertThat(path.properties())
					.containsExactly("customer", "address", "city");
		}

		@Test
		@DisplayName("Should throw exception for null array")
		void shouldThrowForNullArray() {
			assertThatThrownBy(() -> PropertyPath.of((String[]) null))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("at least one property");
		}

		@Test
		@DisplayName("Should throw exception for empty array")
		void shouldThrowForEmptyArray() {
			assertThatThrownBy(() -> PropertyPath.of())
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("Factory Method: from()")
	class FromDotSeparated {

		@Test
		@DisplayName("Should correctly split dot-notation string")
		void shouldSplitDotNotation() {
			PropertyPath path = PropertyPath.from("department.manager.name");

			assertThat(path.properties())
					.containsExactly("department", "manager", "name");
		}

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = {" ", "	", "\t", "\n"})
		@DisplayName("Should throw exception for null, empty, or blank strings")
		void shouldThrowForInvalidStrings(String input) {
			assertThatThrownBy(() -> PropertyPath.from(input))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("Should handle single segment without dots")
		void shouldHandleSingleSegment() {
			PropertyPath path = PropertyPath.from("id");
			assertThat(path.properties()).containsExactly("id");
		}
	}

	@Nested
	@DisplayName("Method: asPath()")
	class AsPathMethod {

		@Test
		@DisplayName("Should resolve path correctly")
		@SuppressWarnings("unchecked")
		void shouldResolveSingleLevel() {
			// Arrange
			Root<Object> root = mock(Root.class);
			Path<Object> expectedPath = mock(Path.class);
			when(root.get("id")).thenReturn(expectedPath);

			PropertyPath propertyPath = PropertyPath.of("id");

			// Act
			Path<Long> result = propertyPath.asPath(root);

			// Assert
			assertThat(result).isSameAs(expectedPath);
			verify(root).get("id");
		}

		@Test
		@DisplayName("Should resolve 3-level nested path via chained get() calls")
		@SuppressWarnings("unchecked")
		void shouldResolveThreeLevelNestedPath() {
			// Arrange
			Root<Object> root = mock(Root.class);
			Path<Object> level1 = mock(Path.class, "level1");
			Path<Object> level2 = mock(Path.class, "level2");
			Path<Object> leaf = mock(Path.class, "leaf");

			when(root.get("customer")).thenReturn(level1);
			when(level1.get("address")).thenReturn(level2);
			when(level2.get("city")).thenReturn(leaf);

			PropertyPath propertyPath = PropertyPath.of("customer", "address", "city");

			// Act
			Path<String> result = propertyPath.asPath(root);

			// Assert
			assertThat(result).isSameAs(leaf);
			verify(root).get("customer");
			verify(level1).get("address");
			verify(level2).get("city");
		}
	}
}
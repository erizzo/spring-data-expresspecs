package rizzoweb.spring.jpa.specifications;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationExtensions {

	/**
	 * Null-safe or() for Specifications that tolerates null values for either argument
	 */
	public static <T> Specification<T> safeOr(@Nullable Specification<T> existing, @Nullable Specification<T> additional) {
		if (existing == null) {
			return additional;
		}

		if (additional == null) {
			return existing;
		}

		return existing.or(additional);
	}

	/**
	 * Null-safe and() for Specifications that tolerates null values for either argument
	 */
	public static <T> Specification<T> safeAnd(@Nullable Specification<T> existing, @Nullable Specification<T> additional) {
		if (existing == null) {
			return additional;
		}

		if (additional == null) {
			return existing;
		}

		return existing.and(additional);
	}
}
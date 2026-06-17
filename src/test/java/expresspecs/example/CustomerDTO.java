package expresspecs.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Flat DTO projection of a {@link Customer}, used to demonstrate returning a projection instead of
 * full entities via {@link expresspecs.SpecificationProjections}.
 */
@Getter
@AllArgsConstructor
public class CustomerDTO {

	private Long id;
	private final String name;
	private final boolean isActive;
}

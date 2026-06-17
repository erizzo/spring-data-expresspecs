package expresspecs;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor.SpecificationFluentQuery;

/**
 * Factory methods for the projection function argument of
 * {@code JpaSpecificationExecutor.findBy(Specification, Function)}. Where the rest of this library
 * expresses the {@code WHERE} clause as a {@link org.springframework.data.jpa.domain.Specification},
 * these helpers express how the matching rows are projected and shaped into results, replacing the
 * noisy fluent-query lambda with a single call that reads as a phrase.
 */
public class SpecificationProjections {

	/**
	 * Builds a query function that returns all matching entities projected as the given type,
	 * for use with {@code JpaSpecificationExecutor.findBy(Specification, Function)}.
	 * <p>
	 * The projection type may be a DTO (closed projection bound by constructor arguments) or an
	 * interface (open projection), as supported by Spring Data's
	 * {@link SpecificationFluentQuery#as(Class)}. This lets callers fetch only the columns they need
	 * instead of full entities, without writing a separate query method.
	 * </p>
	 * <b>Example:</b>
	 *
	 * <pre>{@code
	 * List<CustomerDTO> dtos = repository.findBy(isActive(), projectedAs(CustomerDTO.class));
	 * }</pre>
	 *
	 * @param <EntityType> the entity type the underlying query operates on.
	 * @param <ProjectedType> the type each result is projected into.
	 * @param projectedType the class to project each matching entity into.
	 * @return a function that, given the fluent query, returns the projected results as a {@link List}.
	 */
	public static <EntityType, ProjectedType> @NonNull Function<SpecificationFluentQuery<EntityType>, List<ProjectedType>> projectedAs(@NonNull Class<ProjectedType> projectedType) {
		return q -> q.as(projectedType).all();
	}

	/**
	 * Builds a query function that returns a page of matching entities projected as the given type,
	 * for use with {@code JpaSpecificationExecutor.findBy(Specification, Function)}.
	 * <p>
	 * This is the paged and sorted counterpart of {@link #projectedAs(Class)}. The supplied
	 * {@link Pageable} controls page size, offset, and sort order, and the result carries the usual
	 * pagination metadata. The projection type may be a DTO or an interface, as supported by Spring
	 * Data's {@link SpecificationFluentQuery#as(Class)}.
	 * </p>
	 * <b>Example:</b>
	 *
	 * <pre>{@code
	 * Page<CustomerDTO> dtos = repository.findBy(isActive(), projectedAs(CustomerDTO.class, pageable));
	 * }</pre>
	 *
	 * @param <EntityType> the entity type the underlying query operates on.
	 * @param <ProjectedType> the type each result is projected into.
	 * @param projectedType the class to project each matching entity into.
	 * @param page the paging and sorting information to apply.
	 * @return a function that, given the fluent query, returns the projected results as a {@link Page}.
	 */
	public static <EntityType, ProjectedType> @NonNull Function<SpecificationFluentQuery<EntityType>, Page<ProjectedType>> projectedAs(@NonNull Class<ProjectedType> projectedType, @NonNull Pageable page) {
		return q -> q.as(projectedType).page(page);
	}

	/**
	 * Builds a query function that returns the single matching entity projected as the given type,
	 * for use with {@code JpaSpecificationExecutor.findBy(Specification, Function)}.
	 * <p>
	 * The projection type may be a DTO or an interface, as supported by Spring Data's
	 * {@link SpecificationFluentQuery#as(Class)}. The result is empty when nothing matches; an
	 * exception is thrown when more than one row matches.
	 * </p>
	 * <b>Example:</b>
	 *
	 * <pre>{@code
	 * Optional<CustomerDTO> dto = repository.findBy(hasEmail(email), oneProjectedAs(CustomerDTO.class));
	 * }</pre>
	 *
	 * @param <EntityType> the entity type the underlying query operates on.
	 * @param <ProjectedType> the type the result is projected into.
	 * @param projectedType the class to project the matching entity into.
	 * @return a function that, given the fluent query, returns the single projected result as an {@link Optional}.
	 */
	public static <EntityType, ProjectedType> @NonNull Function<SpecificationFluentQuery<EntityType>, Optional<ProjectedType>> oneProjectedAs(@NonNull Class<ProjectedType> projectedType) {
		return q -> q.as(projectedType).one();
	}

	/**
	 * Builds a query function that returns the first matching entity projected as the given type,
	 * for use with {@code JpaSpecificationExecutor.findBy(Specification, Function)}.
	 * <p>
	 * Unlike {@link #oneProjectedAs(Class)}, this tolerates multiple matches and returns the first,
	 * so it is typically paired with a sort. The projection type may be a DTO or an interface, as
	 * supported by Spring Data's {@link SpecificationFluentQuery#as(Class)}. The result is empty when
	 * nothing matches.
	 * </p>
	 * <b>Example:</b>
	 *
	 * <pre>{@code
	 * Optional<CustomerDTO> dto = repository.findBy(isActive(), firstProjectedAs(CustomerDTO.class));
	 * }</pre>
	 *
	 * @param <EntityType> the entity type the underlying query operates on.
	 * @param <ProjectedType> the type the result is projected into.
	 * @param projectedType the class to project the matching entity into.
	 * @return a function that, given the fluent query, returns the first projected result as an {@link Optional}.
	 */
	public static <EntityType, ProjectedType> @NonNull Function<SpecificationFluentQuery<EntityType>, Optional<ProjectedType>> firstProjectedAs(@NonNull Class<ProjectedType> projectedType) {
		return q -> q.as(projectedType).first();
	}

	/**
	 * Builds a query function that returns the matching entities projected as the given type as a
	 * lazily-evaluated {@link Stream}, for use with
	 * {@code JpaSpecificationExecutor.findBy(Specification, Function)}.
	 * <p>
	 * The projection type may be a DTO or an interface, as supported by Spring Data's
	 * {@link SpecificationFluentQuery#as(Class)}. The returned stream is backed by an open result set
	 * and must be consumed within the surrounding transaction and closed when done, for example with
	 * a try-with-resources block.
	 * </p>
	 * <b>Example:</b>
	 *
	 * <pre>{@code
	 * try (Stream<CustomerDTO> dtos = repository.findBy(isActive(), streamProjectedAs(CustomerDTO.class))) {
	 *     dtos.forEach(...);
	 * }
	 * }</pre>
	 *
	 * @param <EntityType> the entity type the underlying query operates on.
	 * @param <ProjectedType> the type each result is projected into.
	 * @param projectedType the class to project each matching entity into.
	 * @return a function that, given the fluent query, returns the projected results as a {@link Stream}.
	 */
	public static <EntityType, ProjectedType> @NonNull Function<SpecificationFluentQuery<EntityType>, Stream<ProjectedType>> streamProjectedAs(@NonNull Class<ProjectedType> projectedType) {
		return q -> q.as(projectedType).stream();
	}

}

# JPA Specifications
The `...jpa.specifications` package includes classes to make working with the Spring Data JPA Specifications easier for common use cases.

A typical way to use Spring Data JPA repositories is to define query methods in an interface that extends one of the base Spring repository interfaces.
These can be queries [derived from the method name](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.query-creation) 
or [via  `@Query` annotation using JPQL](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.at-query). While convenient
and easy for simple querying scenarios, this technique can become cubersome and verbose when the queries get numerous or need to support multiple and/or optional
parameters. For example, an API to search for `Customer` entities might allow the caller to filter the results by different properties, some of them in nested
child entities. You could easily end up with a derived query method like this:

```java
    Page<Customer> findByAddressZipCodeInOrCreditLimitGreaterThanAndIsActive(List<String> zipCodes, int minCredit, Pageable page);
```
Query methods like this can be very cumbersome and difficult to maintain, espeically as the set of queryable properties grows. Even for a relatively small number
of parameters, these methods can be difficult to grok.

Specifications, on the other hand, offer a composable model of querying that uses JPA's criteria API under the covers. The entry point to using Spring Data
Specifications are methods on the interface `org.springframework.data.jpa.repository.JpaSpecificationExecutor<T>`. The methods in that interface, which can be
included in the super-types of your repository interfaces, all take a `Specification<YourEntity>` to represent the query spec.

Constructing `Specification` objects is extremely flexible but somewhat awkward for common scenarios, which is where this package's helpers come in handy.

## Examples
Let's use the following example entity model for demonstration. 
> [!NOTE]
> These entities don't necessarily represent the _best_ way to define entities for any production
> system, they've been written to help demonstrate the usefullness of Specifications and this package.

```java
@Entity
@Table(name = "customers")
public class Customer {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private Integer creditLimit = 100;

    private OffsetDateTime createdTimestamp;

    @OneToOne
    private Address address;

    @Embedded
    private PhoneNumber phoneNumber;

    @OneToMany(mappedBy = Order.Fields.customer, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(NONE)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();


    public void addOrder(Order order) {
        orders.add(order);
        order.setCustomer(this);
    }

    public void removeOrder(Order order) {
        if (orders.remove(order)) {
            order.setCustomer(null);
        }
    }
}

@Entity
@Table(name = "customer_addresses")
public class Address {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private boolean isPOBox;
}

@Embeddable
public class PhoneNumber {

	private int areaCode;
	private int number;
	private Integer extension;
}

@Entity
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue
    private Long id;

    @EqualsAndHashCode.Include
    private final UUID orderID = UUID.randomUUID();

    private LocalDate datePlaced;

    @ManyToOne
    @Setter(PACKAGE)
    private Customer customer;
}

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}
```

Using the categorical specification classes, we can define queries such as the following:

> Find customers where the `name` contains any of a set of strings
```java
List<String> searchTerms = List.of("Elmer", "Wile", "Bugs");
Specification<Customer> spec = StringSpecifications.containsAny("name", searchTerms);
List<Customer> results = repo.findAll(spec);
```

> Customers whose address has a specific zip code
```java
String zipCode = "99762";
Specification<Customer> spec = BasicSpecifications.is("address.zipCode", zipCode);
``` 


> Customers that don't have a phone number
```java
Specification<Customer> spec = BasicSpecifications.isNull("phoneNumber");
``` 

> Customers who placed an order on a specific date
```java
var targetDate = LocalDate.parse("2025-04-01");
Specification<Customer> spec = DateTimeSpecifications.onDate("orders.datePlaced", targetDate);
```

## Domain specific factory methods
While useful in simplifying `Specification` creation for common cases, the true expressiveness of the general-purpose methods in this library is even more apparent if you define domain-specific
factory methods for your entities (combined with Lombok's `@FieldNameConstants` annotation). For example:

```java
public interface CustomerSpecifications {

	public static Specification<Customer> nameIs(String name) {
		return BasicSpecifications.is(Customer.Fields.name, name);
	}

	public static Specification<Customer> nameIsOneOf(Collection<String> names) {
		return BasicSpecifications.isAny(Customer.Fields.name, names);
	}

	public static Specification<Customer> nameContains(String partialName) {
		return StringSpecifications.contains(Customer.Fields.name, partialName);
	}

	public static Specification<Customer> isActive() {
		return BasicSpecifications.isTrue(Customer.Fields.isActive);
	}

	public static Specification<Customer> isNotActive() {
		return BasicSpecifications.isFalse(Customer.Fields.isActive);
	}

	public static Specification<Customer> isActive(boolean value) {
		return BasicSpecifications.is(Customer.Fields.isActive, value);
	}

	public static Specification<Customer> hasZipCode(String zipCode) {
		final var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		return BasicSpecifications.is(path, zipCode);
	}

	public static Specification<Customer> hasZipCode(Set<String> zipCodes) {
		final var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
		return BasicSpecifications.isAny(path, zipCodes);
	}

	public static Specification<Customer> creditLimitOver(Integer minimum) {
		return RangeSpecifications.greaterThan(Customer.Fields.creditLimit, minimum);
	}

	public static Specification<Customer> hasRecentOrder() {
		final var path = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
		Specification<Customer> spec = RangeSpecifications.atLeast(path, LocalDate.now().minusDays(30));
		return JPASpecificationUtils.smartDistinct(spec);
	}
}
```

Using these kinds of factory methods (along with some static imports) makes the calling code even more expressive
and intentional:

```java
var zipCodes = Set.of("99762", "48169");
List<Customer> results = repo.findAll(
								isActive()
								.and(hasZipCode(zipCodes))
								.and(creditLimitOver(1000)));
```

Compare that to derived query method at the top of this page. 

In addition to this form expressing the domain language very well, it also lends itself
to the parameters being optional. For example, you can imagine a service method similar to this:
```java
public Page<Customer> findSpecialCustomers(boolean isActive, Set<String> zipCodes, Integer minCredit, Pageable pageable) {
	var spec = isActive(isActive);
	
	if (CollectionUtils.isNotEmpty(zipCodes)) {
		spec = spec.and(hasZipCode(zipCodes));
	}
	
	if (minCredit != null) {
		spec = spec.and(creditLimitOver(minCredit));
	}
	
	return repo.findAll(spec, pageable);
}
```

## Implementation Notes

### Spring Boot 3 and 4 Compatibility

This library supports both **Spring Boot 3.5** and **Spring Boot 4.0** from a single codebase.

#### Why

The library's production code (`BasicSpecifications`, `StringSpecifications`, `RangeSpecifications`, etc., along with `SpecificationExtensions` and `PropertyPath`) depends only on the JPA Criteria API and Spring Data JPA's `Specification` interface—APIs that are identical across both Spring Boot versions. No version-specific code is needed at runtime.

The challenge is in the **test infrastructure**. Spring Boot 4 relocated several key test classes to new packages:

| Class | Spring Boot 3 package | Spring Boot 4 package |
|-------|----------------------|----------------------|
| `@DataJpaTest` | `o.s.boot.test.autoconfigure.orm.jpa` | `o.s.boot.data.jpa.test.autoconfigure` |
| `TestEntityManager` | `o.s.boot.test.autoconfigure.orm.jpa` | `o.s.boot.jpa.test.autoconfigure` |
| `@EntityScan` | `o.s.boot.autoconfigure.domain` | `o.s.boot.persistence.autoconfigure` |

Since the test configuration classes must import these types directly, it is not possible for a single test class to compile against both versions.

#### How

The project uses **Maven profiles** (`sb3` and `sb4`) with **separate test source trees** that share common base classes:

```
src/
├── main/java/                  # Production code — version-independent
├── test/java/                  # Common test code (base classes, entities, unit tests)
├── test-springboot3/java/      # SB3-specific: thin subclasses + config
└── test-springboot4/java/      # SB4-specific: thin subclasses + config
```

Each profile activates its corresponding test source tree via `build-helper-maven-plugin` and sets the appropriate `spring-boot.version` property:

- **`sb4`** (default) — uses Spring Boot 4.0, adds `src/test-springboot4/java`
- **`sb3`** — uses Spring Boot 3.5, adds `src/test-springboot3/java`

**Common test code** (`src/test/java`) contains:
- All test entities, repositories, and domain-specific specifications (in the `example` subpackage)
- Abstract base test classes (e.g., `BaseJPASpecificationsIntegrationTests`) that hold the actual `@Test` methods
- An `EntityManagerWrapper` interface that abstracts away the `TestEntityManager` API differences
- Pure unit tests that don't require Spring context (e.g., `SpecificationTests`, `SpecificationExtensionsTests`)

**Version-specific test code** (e.g., `src/test-springboot4/java`) contains only:
- A `@Configuration` class that imports the correct `@EntityScan`, `TestEntityManager`, etc. and provides the `EntityManagerWrapper` bean
- Thin, empty subclasses of the base test classes annotated with the correct version of `@DataJpaTest` and `@ContextConfiguration`

This keeps version-specific code to a minimum—typically just a config class and a one-line test subclass per test suite—while the actual test logic is written and maintained only once.

#### Running the tests

```bash
./mvnw test -Psb4   # Spring Boot 4 (default)
./mvnw test -Psb3   # Spring Boot 3.5
```

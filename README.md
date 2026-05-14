# Spring Data Expresspecs

> The name is a mashup of *express* and *specs* — capturing two ideas at once: the specs are
> *expressive* (readable, intent-revealing factory methods instead of raw `CriteriaBuilder` noise),
> and they're like an *express* train (fast, direct) to Specification/Criteria queries without boilerplate.

This library provides a set of highly expressive, composable factory methods that eliminate the boilerplate of Spring Data JPA Specifications and the underlying Criteria API.

## Why this library?

- **Eliminates Boilerplate:** Say goodbye to verbose `CriteriaBuilder` code for common queries.
- **Automatic Joins:** Easily query across entity relationships using simple dot-notation (e.g., `"address.zipCode"`). The library handles the JPA `Join` for you.
- **Type-safe & Composable:** Create a domain-specific vocabulary of reusable query fragments.
- **Spring Boot 3 & 4 Compatible:** A single, consistent API that works seamlessly across major Spring Boot versions.
- **Smart Distinct:** Avoids the tricky Spring Data pagination `count` bugs by only applying `DISTINCT` when a Join is present and it is safe to do so.

## The Problem

A typical way to use Spring Data JPA is to define query methods derived from the method name, or by writing custom `@Query` JPQL statements:

```java
// Method Name Derivation
Page<Customer> findByAddressZipCodeInOrCreditLimitGreaterThanAndIsActive(List<String> zipCodes, int minCredit, Pageable page);

// JPQL Annotation
@Query("SELECT c FROM Customer c LEFT JOIN c.address a WHERE a.zipCode IN :zipCodes OR c.creditLimit > :minCredit AND c.isActive = true")
Page<Customer> findSpecialCustomers(List<String> zipCodes, int minCredit, Pageable page);
```

As the number of optional search parameters grows, both of these approaches quickly become unmaintainable. You are forced to either write absurdly long method names or create a combinatorial explosion of separate repository methods to handle every possible combination of filters your API might receive.

## Raw Spring JPA Specifications
Writing JPA `Specification` objects manually is powerful, but visually noisy and awkward to construct for simple scenarios. This gets exponentially worse when you need to navigate a relationship using a join:

```java
// The "Raw CriteriaBuilder" way
public static Specification<Customer> hasZipCode(String zipCode) {
    return (root, query, cb) -> {
        Join<Customer, Address> addressJoin = root.join("address");
        return cb.equal(addressJoin.get("zipCode"), zipCode);
    };
}
```

## The Solution

This library gives you clean, declarative factory methods categorized by type.

### Before vs. After

```java
import static expresspecs.StringSpecifications.contains;

// Raw JPA
Specification<Customer> spec = (root, query, cb) -> cb.like(root.get(Customer.Fields.name), "%Bugs%");

// With Spring Data Expresspecs
Specification<Customer> spec = contains(Customer.Fields.name, "Bugs");
```

> [!NOTE]
> Throughout these examples, we use static constants like `Customer.Fields.name`—easily generated via Lombok's [@FieldNameConstants](https://projectlombok.org/features/experimental/FieldNameConstants)—to ensure type-safety and avoid magic strings.
>
> **Alternative: JPA Static Metamodel**
>
> If your project already uses the [JPA static metamodel](https://hibernate.org/orm/tooling/) (e.g., `hibernate-processor`), `PropertyPath.of()` accepts metamodel attributes directly. The typed overloads also validate that path segments chain correctly — `Customer_.address` produces an `Address`, so the compiler enforces that the next segment must be an attribute of `Address`:
>
> ```java
> // Single-segment: same refactoring safety as @FieldNameConstants
> Specification<Customer> spec = is(PropertyPath.of(Customer_.name), "Bugs Bunny");
>
> // Multi-segment through a to-one association — compiler verifies the chain
> var zipPath = PropertyPath.of(Customer_.address, Address_.zipCode);
>
> // Multi-segment through a to-many association — element type (Order) is extracted automatically
> var datePath = PropertyPath.of(Customer_.orders, Order_.datePlaced);
> ```
>
> To enable the metamodel processor with Maven, add `hibernate-processor` alongside Lombok in your compiler plugin configuration:
>
> ```xml
> <annotationProcessorPaths>
>     <path>
>         <groupId>org.projectlombok</groupId>
>         <artifactId>lombok</artifactId>
>         <version>${lombok.version}</version>
>     </path>
>     <path>
>         <groupId>org.hibernate.orm</groupId>
>         <artifactId>hibernate-processor</artifactId>
>     </path>
> </annotationProcessorPaths>
> ```
>
> Both approaches (`@FieldNameConstants` and the static metamodel) work with this library and can coexist within the same project.

## Getting Started

### Dependency

**Maven:**

```xml
<dependency>
    <groupId>com.rizzoweb</groupId>
    <artifactId>spring-data-expresspecs</artifactId>
    <version>0.1</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'com.rizzoweb:spring-data-expresspecs:0.1'
```

Snapshot builds are available from the Maven Central snapshot repository:

```xml
<repositories>
    <repository>
        <id>central-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
```

Then use version `0.2-SNAPSHOT`.

### Compatibility

**Spring Boot:** Supports Spring Boot 3.5 and 4.0.

**Databases:** This library uses only standard JPA Criteria API and Hibernate, so it is compatible with any database that Hibernate supports — not just the ones listed below. The table shows what is actively verified against (using [Testcontainers](https://testcontainers.com/)) using the full test suite on every build:

| Database | Testcontainers Image |
|---|---|
| PostgreSQL | `postgres:17-alpine` |
| MySQL | `mysql:8.4` |
| MariaDB | `mariadb:11.4` |
| Microsoft SQL Server | `mcr.microsoft.com/mssql/server:2022-latest` |
| Oracle | `gvenzl/oracle-free:23-slim-faststart` |

If you want to run the test suite against a different database version, these images can be overridden. See [Overriding Testcontainers images](TESTING.md#overriding-testcontainers-images) in TESTING.md for details.

### Your JPA Repository

**Important:** To execute `Specification`s, your Spring Data repository interface must extend `JpaSpecificationExecutor<T>` in addition to your standard repository extension.

```java
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}
```

## Core Features & Usage

Let's use the following basic queries to demonstrate. Note how easy it is to traverse relationships using either simple dot-notation strings or `PropertyPath.of()` for type-safety.

### 1. Basic Specifications
For standard equality, null checks, and booleans.

```java
import static expresspecs.BasicSpecifications.*;

// Simple equality
Specification<Customer> spec = is(Customer.Fields.isActive, true);

// Automatic joins using simple dot-notation
Specification<Customer> spec = is("address.zipCode", "99762");

// In-clauses
var statePath = PropertyPath.of(Customer.Fields.address, Address.Fields.state);
Specification<Customer> spec = isAny(statePath, Set.of("FL", "MI", "TX"));
```

### 2. String Specifications
For text-based searching (LIKE clauses, ignore case, etc.). *Note: All `StringSpecifications` methods automatically handle appending wildcard characters (`%`) and properly escaping SQL `LIKE` special characters (like `_` and `%`) in your search terms to prevent query errors or unintended wildcard matches.*

```java
import static expresspecs.StringSpecifications.*;

Specification<Customer> spec = containsIgnoreCase(Customer.Fields.name, "bugs");

var cityPath = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
Specification<Customer> spec = startsWith(cityPath, "New");
```

### 3. Range & DateTime Specifications
For numbers, dates, and comparisons.

```java
import static expresspecs.RangeSpecifications.*;
import static expresspecs.datetime.DateTimeSpecifications.*;

Specification<Customer> spec = greaterThan(Customer.Fields.creditLimit, 1000);

var datePath = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
Specification<Customer> spec = onDate(datePath, LocalDate.now());
```

> [!NOTE]
> Some methods in `DateTimeSpecifications` require Hibernate as the JPA provider and will throw an
> exception at runtime with any other provider. See the `DateTimeSpecifications` Javadoc for details.

#### `onDate` behavior by property type

`onDate` constructs the appropriate SQL predicate based on the Java type of the mapped entity property. The behavior differs by type because different temporal types carry different amounts of information.

| Property type | Predicate |
|---|---|
| `LocalDate` | Equality: property equals `targetDate` |
| `java.sql.Date` | Equality: property equals the SQL-date equivalent of `targetDate` |
| `Instant` | UTC half-open range: `[targetDate 00:00 UTC, targetDate+1 00:00 UTC)` |
| `OffsetDateTime` | UTC half-open range (same UTC window, expressed as `OffsetDateTime` at `+00:00`) |
| `ZonedDateTime` | UTC half-open range (same UTC window, expressed as `ZonedDateTime` at UTC) |
| `java.util.Date` / `java.sql.Timestamp` | UTC half-open range (same UTC window, compared as `java.util.Date`) |
| `LocalDateTime` | Wall-clock half-open range: `[targetDate at midnight, targetDate+1 at midnight)` — no zone conversion |

**Date-only types** (`LocalDate`, `java.sql.Date`) use a simple equality check because the stored value already represents just a calendar date.

**Zone-aware types** (`Instant`, `OffsetDateTime`, `ZonedDateTime`, `java.util.Date`, `java.sql.Timestamp`) use a UTC midnight-to-midnight window. "On 2025-03-15" means any instant in `[2025-03-15T00:00:00Z, 2025-03-16T00:00:00Z)`. A value stored as `2025-03-16T01:00+02:00` (which is `2025-03-15T23:00Z`) matches; a value stored as `2025-03-16T00:00:00Z` does not.

**Zone-naive types** (`LocalDateTime`) compare the stored value directly against the wall-clock midnight boundaries with no zone conversion. `2025-03-15T23:45` matches, `2025-03-16T00:00` does not — regardless of where the server or database is located.

> [!NOTE]
> If the property type is not one of the above, `onDate` falls back to wall-clock `LocalDateTime` bounds. Whether the resulting predicate behaves correctly depends on how your JPA provider coerces `LocalDateTime` values to the mapped column type.

## Best Practice: Domain-Specific Factories

While using the library's utility methods directly is great, the true expressiveness shines when you wrap them in domain-specific factory methods for your entities. This creates a clean, type-safe DSL (Domain Specific Language) for your application code.

```java
import static expresspecs.BasicSpecifications.*;
import static expresspecs.RangeSpecifications.*;
import static expresspecs.SpecificationExtensions.smartDistinct;

public interface CustomerSpecifications {

    static Specification<Customer> isActive() {
        return isTrue(Customer.Fields.isActive);
    }

    static Specification<Customer> hasZipCode(Collection<String> zipCodes) {
        // PropertyPath is a type-safe way to represent nested paths like "address.zipCode"
        var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
        return isAny(path, zipCodes);
    }

    static Specification<Customer> creditLimitOver(Integer minimum) {
        return greaterThan(Customer.Fields.creditLimit, minimum);
    }

    static Specification<Customer> hasRecentOrder() {
        var path = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
        Specification<Customer> spec = atLeast(path, LocalDate.now().minusDays(30));
        // Prevent duplicate results when joining on a OneToMany relationship!
        return smartDistinct(spec);
    }
}
```

Using these factory methods with static imports makes your service layer incredibly expressive. Traditionally, handling optional API search parameters requires checking for nulls or empty collections before appending to the query:

```java
public Page<Customer> findSpecialCustomers(Set<String> zipCodes, Integer minCredit, Pageable pageable) {
    var spec = isActive();
    
    if (!CollectionUtils.isEmpty(zipCodes)) {
        spec = spec.and(hasZipCode(zipCodes));
    }
    
    if (minCredit != null) {
        spec = spec.and(creditLimitOver(minCredit));
    }
    
    return customerRepository.findAll(spec, pageable);
}
```

### Streamlining Optional Filters

One of the hidden superpowers of this library is that **it safely handles null and empty inputs automatically**. If you pass `null` or an empty collection into the factory methods, they safely return an "unrestricted" specification that acts as a no-op when chained. 

This means you can drop the `if` statements entirely and collapse your search APIs into a perfectly fluid chain:

```java
public Page<Customer> findSpecialCustomers(Set<String> zipCodes, Integer minCredit, Pageable pageable) {
    var spec = isActive()
            .and(hasZipCode(zipCodes))
            .and(creditLimitOver(minCredit));
    
    return customerRepository.findAll(spec, pageable);
}
```

> [!NOTE]
> Most factory methods behave this way, though there are a few exceptions. Consult the Javadoc on each method for details.

### Complete Example Source Code

To see a complete, fully working example of how all these pieces fit together, check out the `example` package in our test suite. It contains the exact code that runs our integration tests:

- **Domain Model:** [Customer.java](src/test/java/expresspecs/example/Customer.java) (and its related [Order.java](src/test/java/expresspecs/example/Order.java) / [Address.java](src/test/java/expresspecs/example/Address.java) entities)
- **Repository:** [CustomerRepository.java](src/test/java/expresspecs/example/CustomerRepository.java)
- **DSL Factory:** [CustomerSpecifications.java](src/test/java/expresspecs/example/CustomerSpecifications.java)
- **Service Layer:** [CustomersService.java](src/test/java/expresspecs/example/CustomersService.java)


## The Magic of `smartDistinct`

When you join across a `OneToMany` collection (like searching for a `Customer` who has `orders` placed after a certain date), JPA will often return duplicate `Customer` rows. 

Adding `.distinct(true)` solves the duplicate rows, but notoriously breaks Spring Data's pagination `count` query by attempting to apply a distinct to the `Long` count, throwing an exception.

`SpecificationExtensions.smartDistinct(spec)` safely wraps your specification. It detects if a join is present and only applies the `DISTINCT` keyword if the return type is the entity itself, automatically avoiding it during count queries.


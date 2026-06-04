# Spring Data ExpresSpecs | Usage guide

This guide covers setup, compatibility, typing options, factory usage, edge cases, and patterns. For the high-level pitch and a quick before/after, see the [README](../README.md).

## Table of contents

- [Dependency](#dependency)
- [Compatibility](#compatibility)
- [Your JPA Repository](#your-jpa-repository)
- [How to specify a property path](#how-to-specify-a-property-path)
- [Best Practice: Domain-Specific Factories](#best-practice-domain-specific-factories)
- [Core Features & Usage](#core-features--usage)
  - [Basic Specifications](#basic-specifications)
  - [String Specifications](#string-specifications)
  - [Range & DateTime Specifications](#range--datetime-specifications)
    - [`onDate` behavior by property type](#ondate-behavior-by-property-type)
  - [Collection Specifications](#collection-specifications)
- [Streamlining Optional Filters](#streamlining-optional-filters)
- [The Magic of `smartDistinct`](#the-magic-of-smartdistinct)
- [Complete Example Source Code](#complete-example-source-code)

## Dependency

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

## Compatibility

**Spring Boot:** Supports Spring Boot 3.5 and 4.0.

**Databases:** This library uses only standard JPA Criteria API and Hibernate, so it is compatible with any database that Hibernate supports (not just the ones listed below). The table shows what is actively verified against (using [Testcontainers](https://testcontainers.com/)) using the full test suite on every build:

| Database             | Testcontainers Image                         |
| -------------------- | -------------------------------------------- |
| PostgreSQL           | `postgres:17-alpine`                         |
| MySQL                | `mysql:8.4`                                  |
| MariaDB              | `mariadb:11.4`                               |
| Microsoft SQL Server | `mcr.microsoft.com/mssql/server:2022-latest` |
| Oracle               | `gvenzl/oracle-free:23-slim-faststart`       |

If you want to run the test suite against a different database version, these images can be overridden. See [Overriding Testcontainers images](testing.md#overriding-testcontainers-images) in docs/testing.md for details.

## Your JPA Repository

**Important:** To execute `Specification`s, your Spring Data repository interface must extend `JpaSpecificationExecutor<T>` in addition to your standard repository extension.

```java
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}
```

## How to specify a property path

Most factory methods in this library accept a property path argument that identifies which entity field a predicate operates on. Property paths can be simple (`name`) or traverse associations using dot notation (`address.zipCode`, `orders.datePlaced`); the library resolves each segment automatically, joining across associations as needed.

The path can be either a plain `String` or a `PropertyPath` built from an array of string segments. Those segments are typically string literals or [Lombok-generated field name constants](https://projectlombok.org/features/experimental/FieldNameConstants) (which are also strings).

```java
import static expresspecs.BasicSpecifications.*;

// Plain string literal for a direct field
Specification<Customer> spec = is("name", "Bugs Bunny");

// Lombok @FieldNameConstants constant for the same field (refactoring-safe)
Specification<Customer> spec = is(Customer.Fields.name, "Bugs Bunny");

// Dot-notation string to traverse an association
Specification<Customer> spec = is("address.zipCode", "99762");

// PropertyPath built from Lombok constants for a multi-segment path
var statePath = PropertyPath.of(Customer.Fields.address, Address.Fields.state);
Specification<Customer> spec = isAny(statePath, Set.of("FL", "MI", "TX"));
```

If your project already uses the [JPA static metamodel](https://hibernate.org/orm/tooling/) (e.g., the `hibernate-processor` Maven plugin), `PropertyPath.of()` accepts metamodel attributes directly. The typed overloads also validate that path segments chain correctly: `Customer_.address` produces an `Address`, so the compiler enforces that the next segment must be an attribute of `Address`:

```java
// Single-segment: same refactoring safety as @FieldNameConstants
Specification<Customer> spec = is(PropertyPath.of(Customer_.name), "Bugs Bunny");

// Multi-segment through a to-one association, compiler verifies the chain
var zipPath = PropertyPath.of(Customer_.address, Address_.zipCode);

// Multi-segment through a to-many association, element type (Order) is extracted automatically
var datePath = PropertyPath.of(Customer_.orders, Order_.datePlaced);
```

What's important here is that ExpresSpecs gives you flexibility in how you specify the entity properties. Choose the most readable form for you and your entity model. All the ways of specifying property paths can even co-exist within the same project - different calls to the factory methods can use different forms if you want.

## Best Practice: Domain-Specific Factories

While using the library's utility methods directly is great, the true expressiveness shines when you wrap them in domain-specific factory methods for your entities. This creates a clean, type-safe DSL (Domain Specific Language) for your application code.

```java
import static expresspecs.BasicSpecifications.*;
import static expresspecs.RangeSpecifications.*;

public interface CustomerSpecifications {

    static Specification<Customer> isActive() {
        return isTrue(Customer.Fields.isActive);
    }

    static Specification<Customer> hasZipCode(Collection<String> zipCodes) {
        var path = PropertyPath.of(Customer.Fields.address, Address.Fields.zipCode);
        return isAny(path, zipCodes);
    }

    static Specification<Customer> creditLimitOver(Integer minimum) {
        return greaterThan(Customer.Fields.creditLimit, minimum);
    }

    static Specification<Customer> hasRecentOrder() {
        var path = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
        return atLeast(path, LocalDate.now().minusDays(30));
    }
}
```

Using these factory methods with static imports makes your service layer incredibly expressive. 

```java
public Page<Customer> findSpecialCustomers(Set<String> zipCodes, Integer minCredit, Pageable pageable) {
    var spec = isActive()
               .and(hasZipCode(zipCodes))
               .and(creditLimitOver(minCredit));

    return customerRepository.findAll(spec, pageable);
}
```

### Core Features & Usage

The library's factory methods are organized into several classes by predicate type. The examples below show the most common ones.

### Basic Specifications

For standard equality, null checks, and booleans.

```java
import static expresspecs.BasicSpecifications.*;

// Equality
Specification<Customer> spec = is(Customer.Fields.name, "Bugs Bunny");

// Automatic joins using dot-notation
Specification<Customer> spec = is("address.zipCode", "99762");

// Boolean predicates
Specification<Customer> spec = isTrue(Customer.Fields.isActive);
Specification<Customer> spec = isFalse(Customer.Fields.isActive);

// Null checks
Specification<Customer> spec = isNull(Customer.Fields.creditLimit);
Specification<Customer> spec = notNull(Customer.Fields.creditLimit);

// IN clause
var statePath = PropertyPath.of(Customer.Fields.address, Address.Fields.state);
Specification<Customer> spec = isAny(statePath, Set.of("FL", "MI", "TX"));

// Column-to-column comparison
Specification<Order> spec = areEqual("promisedDate", "shippedDate");
```

Unlike the other methods, `areEqual` compares two entity properties against each other rather than a property against a fixed value. It is useful when the filter criterion is a relationship between two columns on the same row, such as finding orders whose promised delivery date matches the actual ship date.

For all available predicates and their descriptions, see [BasicSpecifications.java](../src/main/java/expresspecs/BasicSpecifications.java).

### String Specifications

For text-based searching (`LIKE` clauses, for example). Methods are provided for partial string matching (both case-sensitive and case-insensitive variants), null/emptiness checking, and `equalsIgnoreCase` for case-insensitive equality. For case-sensitive string equality, `BasicSpecifications.is()` works directly.

> [!NOTE]
> All methods that perform partial string matching automatically append wildcard characters (`%`) and escape SQL `LIKE` wildcards (`_` and `%`) in your search terms, preventing query errors or unintended wildcard matches.

Here are some examples; see [StringSpecifications.java](../src/main/java/expresspecs/StringSpecifications.java) for
all available predicates and their descriptions.

```java
import static expresspecs.StringSpecifications.*;

Specification<Customer> spec = containsIgnoreCase(Customer.Fields.name, "bugs");

var cityPath = PropertyPath.of(Customer.Fields.address, Address.Fields.city);
Specification<Customer> spec = doesNotContain(cityPath, "Heights");

Specification<Customer> spec = containsAny(Customer.Fields.name, List.of("Bugs", "Daffy"));

Specification<Customer> spec = startsWithIgnoreCase(Customer.Fields.name, "WILE");

Specification<Customer> spec = isNullOrEmpty("address.zipCode");
Specification<Customer> spec = isNotNullOrEmpty(Customer.Fields.name);
```

> [!WARNING]
> `isNotNullOrEmpty` does not behave correctly on Oracle. Oracle coerces `''` to `NULL` at storage
> time, so the `column <> ''` predicate binds as `column <> NULL`, which is UNKNOWN under SQL
> three-valued logic (`NOT UNKNOWN` is also UNKNOWN), silently excluding every row including those
> with real values. If your application targets Oracle, avoid `isNotNullOrEmpty` and express the
> condition another way (for example, checking only `isNotNull`, since Oracle cannot store an
> empty string and a non-null column value is therefore guaranteed to be non-empty).


### Range & DateTime Specifications

For numbers, dates, and comparisons.

```java
import static expresspecs.RangeSpecifications.*;
import static expresspecs.datetime.DateTimeSpecifications.*;

Specification<Customer> spec = greaterThan(Customer.Fields.creditLimit, 1000);

var datePath = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
Specification<Customer> spec = onDate(datePath, LocalDate.now());

// Customers created in a specific year
Specification<Customer> spec = yearIs(Customer.Fields.createdTimestamp, 2024);
```

> [!NOTE]
> Some methods in `DateTimeSpecifications` require Hibernate as the JPA provider and will throw an
> exception at runtime with any other provider. See the `DateTimeSpecifications` Javadoc for details.

#### `onDate` behavior by property type

`onDate` constructs the appropriate SQL predicate based on the Java type of the mapped entity property. The behavior differs by type because different temporal types carry different amounts of information.

| Property type                           | Predicate                                                                                            |
| --------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| `LocalDate`                             | Equality: property equals `targetDate`                                                               |
| `java.sql.Date`                         | Equality: property equals the SQL-date equivalent of `targetDate`                                    |
| `Instant`                               | UTC half-open range: `[targetDate 00:00 UTC, targetDate+1 00:00 UTC)`                                |
| `OffsetDateTime`                        | UTC half-open range (same UTC window, expressed as `OffsetDateTime` at `+00:00`)                     |
| `ZonedDateTime`                         | UTC half-open range (same UTC window, expressed as `ZonedDateTime` at UTC)                           |
| `java.util.Date` / `java.sql.Timestamp` | UTC half-open range (same UTC window, compared as `java.util.Date`)                                  |
| `LocalDateTime`                         | Wall-clock half-open range: `[targetDate at midnight, targetDate+1 at midnight)`, no zone conversion |

Any other mapped Java type causes `IllegalArgumentException` when the specification runs (for example `java.util.Calendar`).

**Date-only types** (`LocalDate`, `java.sql.Date`) use a simple equality check because the stored value already represents just a calendar date.

**Zone-aware types** (`Instant`, `OffsetDateTime`, `ZonedDateTime`, `java.util.Date`, `java.sql.Timestamp`) use a UTC midnight-to-midnight window. "On 2025-03-15" means any instant in `[2025-03-15T00:00:00Z, 2025-03-16T00:00:00Z)`. A value stored as `2025-03-16T01:00+02:00` (which is `2025-03-15T23:00Z`) matches; a value stored as `2025-03-16T00:00:00Z` does not.

**Zone-naive types** (`LocalDateTime`) compare the stored value directly against the wall-clock midnight boundaries with no zone conversion. `2025-03-15T23:45` matches but `2025-03-16T00:00` does not, regardless of where the server or database is located.

> [!NOTE]
> Unsupported property types throw `IllegalArgumentException` with a message that names the leaf type and lists supported alternatives. Through Spring Data JPA, that exception may be wrapped in a `DataAccessException` (for example `InvalidDataAccessApiUsageException`).

For all available predicates and their descriptions, see [RangeSpecifications.java](../src/main/java/expresspecs/RangeSpecifications.java) and [DateTimeSpecifications.java](../src/main/java/expresspecs/datetime/DateTimeSpecifications.java).

### Collection Specifications

For predicates on mapped collection properties, such as emptiness, membership, and size checks.

```java
import static expresspecs.CollectionSpecifications.*;

// Customers who have placed at least one order
Specification<Customer> spec = isNotEmpty(Customer.Fields.orders);

// Customers with no orders
Specification<Customer> spec = isEmpty(Customer.Fields.orders);

// Customers with three or more orders
Specification<Customer> spec = sizeAtLeast(Customer.Fields.orders, 3);

// Customers who have a specific tag (assuming a @ElementCollection Set<String> tags)
Specification<Customer> spec = containsMember(Customer.Fields.tags, "vip");
```

For all available predicates and their descriptions, see [CollectionSpecifications.java](../src/main/java/expresspecs/CollectionSpecifications.java).

## Streamlining Optional Filters

Traditionally, handling optional API search parameters requires checking for nulls or empty collections before appending to the query:

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

One of the hidden superpowers of this library is that **it safely handles null and empty inputs automatically**. If you pass `null` or an empty collection into the factory methods, they safely return an "unrestricted" specification that acts as a no-op when chained.

This means you can drop the `if` statements entirely and collapse your search APIs into a perfectly fluid chain as we saw above:

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

## The Magic of `smartDistinct`

When you join across a `OneToMany` collection (like searching for a `Customer` who has `orders` placed after a certain date), JPA will often return duplicate `Customer` rows. 

Adding `.distinct(true)` solves the duplicate rows, but notoriously breaks Spring Data's pagination `count` query by attempting to apply a distinct to the `Long` count, throwing an exception.

`SpecificationExtensions.smartDistinct(spec)` safely wraps your specification. It detects if a join is present and only applies the `DISTINCT` keyword if the return type is the entity itself, automatically avoiding it during count queries.

In practice, `smartDistinct` belongs inside the domain-specific factory method that causes the join, so callers never have to think about it:

```java
import static expresspecs.RangeSpecifications.*;
import static expresspecs.SpecificationExtensions.smartDistinct;

public interface CustomerSpecifications {

    static Specification<Customer> hasRecentOrder() {
        var path = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
        Specification<Customer> spec = atLeast(path, LocalDate.now().minusDays(30));
        return smartDistinct(spec);
    }
}
```

## Complete Example Source Code

To see a complete, fully working example of how all these pieces fit together, check out the `example` package in our test suite. It contains the exact code that runs our integration tests:

- **Domain Model:** [Customer.java](../src/test/java/expresspecs/example/Customer.java) (and its related [Order.java](../src/test/java/expresspecs/example/Order.java) / [Address.java](../src/test/java/expresspecs/example/Address.java) entities)
- **Repository:** [CustomerRepository.java](../src/test/java/expresspecs/example/CustomerRepository.java)
- **DSL Factory:** [CustomerSpecifications.java](../src/test/java/expresspecs/example/CustomerSpecifications.java)
- **Service Layer:** [CustomersService.java](../src/test/java/expresspecs/example/CustomersService.java)

# Spring Data Expresspecs

> The name is a mashup of *express* and *specs* — capturing two ideas at once: the specs are
> *expressive* (readable, intent-revealing factory methods instead of raw `CriteriaBuilder` noise),
> and they let you *express* queries quickly, without boilerplate.

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

*(Note: Throughout these examples, we use static constants like `Customer.Fields.name`—easily generated via Lombok's [@FieldNameConstants](https://projectlombok.org/features/experimental/FieldNameConstants)—to ensure type-safety and avoid magic strings).*

## Getting Started

*(Add your Maven/Gradle dependency information here when published)*

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
import static expresspecs.DateTimeSpecifications.*;

Specification<Customer> spec = greaterThan(Customer.Fields.creditLimit, 1000);

var datePath = PropertyPath.of(Customer.Fields.orders, Order.Fields.datePlaced);
Specification<Customer> spec = onDate(datePath, LocalDate.now());
```

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

> [!NOTE]
> Most factory methods behave this way, though there are a few exceptions. Consult the Javadoc on each method for details.

This means you can drop the `if` statements entirely and collapse your search APIs into a perfectly fluid chain:

```java
public Page<Customer> findSpecialCustomers(Set<String> zipCodes, Integer minCredit, Pageable pageable) {
    var spec = isActive()
            .and(hasZipCode(zipCodes))
            .and(creditLimitOver(minCredit));
    
    return customerRepository.findAll(spec, pageable);
}
```

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


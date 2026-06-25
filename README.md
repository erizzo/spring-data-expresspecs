# Spring Data ExpresSpecs

> The name is a mashup of *express* and *specs*, capturing two ideas at once: the specs are
> *expressive* (readable, intent-revealing factory methods instead of raw `CriteriaBuilder` noise),
> and they're like an *express* train (fast, direct) to Specification/Criteria queries without boilerplate.

This library provides a set of highly expressive, composable factory methods that eliminate the boilerplate of Spring Data JPA Specifications and the underlying Criteria API.

To be clear: this is a convenience library, not a framework. The patterns it encodes are not new or revolutionary, but having them pre-built, tested, and composable keeps application code focused on business logic rather than Criteria API mechanics. The resulting code reads like *intent* rather than plumbing.

[![Build](https://github.com/erizzo/spring-data-expresspecs/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/erizzo/spring-data-expresspecs/actions/workflows/build-and-test.yml)
[![Coverage](https://erizzo.github.io/spring-data-expresspecs/badges/jacoco.svg)](https://erizzo.github.io/spring-data-expresspecs/coverage/)

## The Status Quo

The typical way to use Spring Data JPA is to define query methods derived from the method name, or by writing custom `@Query` JPQL statements:

```java
// Method Name Derivation
Page<Customer> findByAddressZipCodeInOrCreditLimitGreaterThanAndIsActive(List<String> zipCodes, int minCredit, Pageable page);

// JPQL Annotation
@Query("SELECT c FROM Customer c LEFT JOIN c.address a WHERE a.zipCode IN :zipCodes OR c.creditLimit > :minCredit AND c.isActive = true")
Page<Customer> findSpecialCustomers(List<String> zipCodes, int minCredit, Pageable page);
```

As the number and optionality of query parameters grows, both of these approaches become clunky and unmaintainable. You're forced to either write
absurdly long method names or create a combinatorial explosion of separate repository methods to handle every possible combination of filters
your application might need.

Spring's JPA `Specification` objects are a powerful and composable alternative, but the code can be visually noisy and awkward to construct, even
for simple scenarios. Once you need to navigate relationships or handle optional parameters, it can be quite verbose.

```java
public Specification<Customer> hasZipCodeOrMinCredit(List<String> zipCodes, int minCredit) {
    return (root, query, cb) -> {
        Join<Customer, Address> address = root.join("address", JoinType.LEFT);
        Predicate active = cb.isTrue(root.get("isActive"));
        Predicate highCredit = cb.greaterThan(root.get("creditLimit"), minCredit);
        Predicate zipBranch;

        if (CollectionUtils.isEmpty(zipCodes)) {
          // IN () is undefined / false; OR reduces to credit branch only
          zipBranch = cb.disjunction();
        } else {
          zipBranch = address.get("zipCode").in(zipCodes);
        }

        Predicate orPart = cb.or(zipBranch, highCredit);
        return cb.and(orPart, active);
    };
}
```

*A contrived example, but you get the point.*

## We Can Do Better

With ExpresSpecs you can quickly build a thin domain-specific query vocabulary on top of its factory methods.
The result reads like natural language and is easy to maintain as query requirements shift and expand.

```java
import static com.example.CustomerSpecifications.*;

public List<Customer> findActiveCustomersByName(String partialName) {
    return repository.findAll(
                        isActive()
                        .and(nameContainsIgnoreCase(partialName)));
}

public List<Customer> findByZipCodeOrMinCredit(String zipCode, int minCredit) {
    return repository.findAll(
                        hasZipCode(zipCode)
                        .or(creditLimitOver(minCredit)));
}
```

Your domain-specific query vocabulary (`CustomerSpecifications` in the example above) is trivial to write, easy to evolve,
and sits entirely in your own codebase. See the [Usage Guide](docs/usage-guide.md) for how to build it and what ExpresSpecs provides underneath.

## Key Features

- **Eliminates Boilerplate:** Say goodbye to verbose `CriteriaBuilder` code for common queries.
- **Automatic Joins:** Easily query across entity relationships using simple dot-notation (e.g., `"address.zipCode"`). The library handles the JPA `Join` for you.
- **Type-safe & Composable:** Create a domain-specific vocabulary of reusable query fragments.
- **Spring Boot 3 & 4 Compatible:** A single, consistent API that works seamlessly across major Spring Boot versions.
- **Smart Distinct:** Avoids the tricky Spring Data pagination `count` bugs by only applying `DISTINCT` when a Join is present and it is safe to do so.

## ExpresSpecs vs. Querydsl

[Querydsl](https://querydsl.com) is the most well-known alternative for type-safe dynamic queries with Spring Data JPA, so it's worth being clear about how ExpresSpecs differs and where each one fits.

Querydsl is a full query DSL. An annotation processor generates `Q`-types, a static metamodel for your entities, and you write entire queries, including joins, projections, subqueries, grouping, and ordering, against that metamodel with compile-time checking of the whole query shape. That's a real investment (build-time code generation, a generated source tree to manage), but it pays off for applications with complex reporting-style queries, DTO projections, or query needs that go well beyond a `WHERE` clause.

ExpresSpecs is intentionally narrower. It generates no code and introduces no metamodel; it's a set of static factory methods that build standard Spring Data `Specification` objects, which are themselves just `CriteriaBuilder` predicates under the hood. It targets the single most common pain point: applications with search/filter screens that need to combine many optional criteria, often across relationships, into a `WHERE` clause.

**Choose ExpresSpecs when:**

- Your queries are primarily filtering: combining a handful of optional criteria (equality, ranges, text search, null checks, collection membership) over one or two entity graphs.
- You're already using, or willing to use, Spring Data's `Specification` / `JpaSpecificationExecutor`, and just want to stop writing `CriteriaBuilder` boilerplate.
- You'd rather not add an annotation processor or generated source step to your build.
- You want a small, focused dependency you can drop into an existing repository method.

**Choose Querydsl when:**

- You need complex projections, such as aggregations, computed columns, or DTOs assembled from multiple joined entities, as first-class parts of the query*.
- You need subqueries, grouping/aggregation, or dynamic ordering as first-class parts of the query.
- You want the entire query, not just the predicate, checked at compile time against your schema.
- Your application's query needs go meaningfully beyond filtering.

The two aren't mutually exclusive: it's reasonable to use ExpresSpecs for filter-building in most repositories and reach for Querydsl (or plain JPQL/native queries) for the handful of reporting-style queries that need more.

*Note that simple DTO/interface projections don't require Querydsl: Spring Data JPA  `JpaSpecificationExecutor` provides `findBy(Specification, queryFunction)` method. Class `SpecificationProjections` provides common, basic query functions that can be used with that method to maintain expressive, simple syntax when projecting. See [Returning Projections](docs/usage-guide.md#returning-projections) in the Usage Guide for details.

## Getting Started

Add the dependency and extend your repository from Spring's `JpaSpecificationExecutor` - that's all that's strictly required to start using ExpresSpecs.
To realize the full benefits of expressive syntax and readability, you'll want to construct your own set of domain-specific methods, what we call the domain query vocabulary. Don't worry, that's
very straightforward and you can start with just what you need right now. The [Usage Guide](docs/usage-guide.md) has all the details, and the test suite
includes a runnable example you can follow.

**Maven**

```xml
<dependency>
    <groupId>com.rizzoweb</groupId>
    <artifactId>spring-data-expresspecs</artifactId>
    <version>0.2.0</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'com.rizzoweb:spring-data-expresspecs:0.2.0'
```

### Your JPA Repository

**Important:** To execute `Specification`s, your Spring Data repository interface must extend `JpaSpecificationExecutor<T>` in addition to your standard repository extension.

```java
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}
```

### Compatibility

**Spring Boot:** Supports Spring Boot 3.5, 4.0, and 4.1. CI verifies all three versions (H2 integration tests) on every build.

**Databases:** This library uses only standard JPA Criteria API and Hibernate, so it's compatible with any database that Hibernate supports (not only the ones
listed below). This table shows what databases the code is *actively verified against* (using [Testcontainers](https://testcontainers.com/)) using the full test
suite on every build:

| Database             | Testcontainers Image                         |
| -------------------- | -------------------------------------------- |
| PostgreSQL           | `postgres:17-alpine`                         |
| MySQL                | `mysql:8.4`                                  |
| MariaDB              | `mariadb:11.4`                               |
| Microsoft SQL Server | `mcr.microsoft.com/mssql/server:2022-latest` |
| Oracle               | `gvenzl/oracle-free:23-slim-faststart`       |

If you want to run the test suite against a different database version, these images can be overridden. See [Overriding Testcontainers images](docs/testing.md#overriding-testcontainers-images) in docs/testing.md for details.

---

For full API reference, usage patterns, and best practices, see the [Usage Guide](docs/usage-guide.md).

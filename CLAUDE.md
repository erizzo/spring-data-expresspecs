# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

A Java library that provides composable, type-safe factory methods for building Spring Data JPA `Specification` objects. It eliminates the boilerplate of raw `CriteriaBuilder` code and is the alternative to method-name derivation or `@Query` annotations when dealing with many optional filters.

## Eclipse / jdt

This project is developed in Eclipse with the `jdt` CLI bridge, which exposes the live IDE workspace to the terminal. Prefer these over Maven for day-to-day work:

```bash
jdt problems                        # check compilation errors after edits
jdt test run <FQN> -f -q            # run a single test class and stream results
jdt build --project spring-data-expresspecs    # trigger incremental build
jdt status -q                       # snapshot of open editors, errors, running tests
```

## Build & test commands

```bash
# Build (default: Spring Boot 4)
./mvnw clean install

# Build with Spring Boot 3 profile
./mvnw clean install -Psb3

# Run all tests
./mvnw test

# Run tests with Spring Boot 3
./mvnw test -Psb3

# Run a single test class
./mvnw test -Dtest=BasicSpecificationsTests

# Run a single test method
./mvnw test -Dtest=BasicSpecificationsTests#testEqualTo
```

## Architecture

### Main source (`src/main/java/expresspecs/`)

- **`PropertyPath`** — the core abstraction. A record representing a dot-notation JPA path (e.g., `"address.zipCode"`). It traverses associations via LEFT JOINs and embeddables via `.get()`, and reuses existing joins to prevent SQL duplicates. Most factory methods accept a `PropertyPath` (or a `String` shorthand for a single attribute).

- **`BasicSpecifications`** — equality, null checks, boolean logic, IN clauses, and `unrestricted()` (a no-op Specification used as the safe null replacement).

- **`StringSpecifications`** — LIKE/contains predicates with automatic escaping of SQL special characters (`_`, `%`) and optional case-insensitive matching.

- **`RangeSpecifications`** — less-than, greater-than, between.

- **`DateTimeSpecifications`** — date-based predicates (e.g., `onDate`).

- **`CollectionSpecifications`** — collection-membership and size predicates.

- **`SpecificationExtensions`** — `safeAnd`, `safeOr`, and `smartDistinct`. `smartDistinct` wraps a Specification to apply DISTINCT only when joins are present *and* the query return type is not `Long`, sidestepping the Spring Data pagination-with-join bug.

- **`SQLUtils`** — internal utility for escaping LIKE wildcards.

### Null/empty safety convention

All factory methods handle null or empty inputs by returning `BasicSpecifications.unrestricted()` rather than `null`. This keeps consumer code free of null-guards and safely chainable.

### Testing

- `src/test/java` — unit tests (mock-based) and Spring Boot 4 integration tests using H2.
- `src/test-springboot3/java` — Spring Boot 3-compatible versions of the integration tests (activated by the `sb3` Maven profile).
- The `example` subpackage contains a worked example domain (Customer, Order, Address) with a `CustomerSpecifications` factory class demonstrating how to build a business-language DSL on top of this library.

### Dual Spring Boot support

The `sb3` Maven profile switches the Spring Boot BOM version and activates the `src/test-springboot3/java` test source tree. When adding new features, verify behaviour under both profiles.

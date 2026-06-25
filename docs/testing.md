# Testing

This document describes how tests are organized, how to run them (including Testcontainers), and how Spring Boot 3 vs 4 is handled in the test harness.

## Spring Boot 3 and 4 compatibility

This library supports **Spring Boot 3.5**, **4.0**, and **4.1** from a single codebase. The production code depends only on the JPA Criteria API and Spring Data JPA's `Specification` interface, APIs that are identical across all supported Spring Boot versions. No version-specific code is needed at runtime.

The challenge is in the **test infrastructure**. Spring Boot 4 relocated several key test classes (like `@DataJpaTest`) to new packages.

### How

The project uses **Maven profiles** (`sb3` and `sb4`) with **separate test source trees** that share common base classes:

```
src/
├── main/java/                    # Production code, version-independent
└── test/
    ├── java/                     # Common test code (base classes, entities, unit tests)
    ├── springboot3/java/         # SB3-specific: thin subclasses + config
    ├── springboot4/java/         # SB4-specific: thin subclasses + config
    ├── containers/java/          # Testcontainers tests (PostgreSQL, MySQL, MariaDB, SQL Server)
    └── containers-oracle/java/   # Testcontainers test for Oracle (separate due to startup cost)
```

**Maven Profiles**
- `sb4` (default): uses Spring Boot 4.0, adds `src/test/springboot4/java`
- `sb3`: uses Spring Boot 3.5, adds `src/test/springboot3/java`
- `tc`: adds Testcontainers dependencies and `src/test/containers/java`; **SB4 only** (see below)
- `oracle`: adds the Oracle Free Testcontainers module and `src/test/containers-oracle/java`; must be combined with `tc`; **SB4 only**

**Spring Boot 4.1 compatibility** is verified in CI by overriding the `sb4` profile's version property on the command line (`-Dspring-boot.version=4.1.0`). No separate Maven profile or source tree is needed since the Spring Boot 4.0-to-4.1 migration introduces no API-level changes relevant to this library.

**Common test code** (`src/test/java`) contains abstract base test classes (e.g., `BaseJPAIntegrationTest`), entities, and an `EntityManagerWrapper`.
Date/time specification factories and their tests live under `expresspecs.datetime` (`src/main/java/expresspecs/datetime/`, `src/test/java/expresspecs/datetime/`).
**Version-specific test code** contains only the Spring configurations and thin, empty subclasses of the base test classes.

## Kinds of tests

The suite has two complementary kinds of tests, both living in `src/test/java`:

- **Unit tests** (`*SpecificationsTests`, plus `PropertyPathTests`, `SQLUtilsTests`): fast, mock-based tests using Mockito. They verify the `Predicate`/`CriteriaBuilder` interactions a factory method produces without touching a database, and they run with no profile or Docker required.
- **Integration tests** (`*IntegrationTests`, extending `BaseJPAIntegrationTest`): execute real queries through Hibernate against an actual database. By default these run on in-memory **H2**; under the `tc` and `oracle` profiles the same tests run against real databases via Testcontainers (see [Why Database-Specific Tests?](#why-database-specific-tests)).

When adding a factory method or fixing a bug, cover it at both levels: a unit test that pins down the predicate construction, and an integration test that proves the generated SQL actually runs and returns the right rows.

## Running the tests

Always use `clean` before `test` (for example `./mvnw clean test` with the profiles below), not `./mvnw test`, so a stale `target/` directory from a previous run doesn't affect test results. This is a consequence of supporting 2 incompatible versions of Spring and the conflicting classes the maven profiles produce.

```bash
# H2 in-memory (fast, no Docker required)
./mvnw clean test           # Spring Boot 4 (default)
./mvnw clean test -Psb3     # Spring Boot 3.5

# Testcontainers: real databases via Docker (requires Docker; SB4 only)
./mvnw clean test -Psb4,tc              # PostgreSQL, MySQL, MariaDB, SQL Server
./mvnw clean test -Psb4,tc,oracle      # adds Oracle Free (~60–90 s startup)
```

> Using `tc` and `oracle`
>
> Run them only with **Spring Boot 4**: `-Psb4,tc` or `-Psb4,tc,oracle`. The Spring Boot 3 BOM manages Testcontainers **1.x**; these profiles use **2.x** from the Boot 4 BOM (different Maven coordinates and Java packages), so `-Psb3,tc` (or `oracle` with `sb3`) is not supported and often fails while resolving dependencies.
>
> Maven disables the `sb4` profile's `activeByDefault` whenever you pass any `-P` list, so include `sb4` in the profile list whenever you add `tc` or `oracle`.

## Overriding Testcontainers images

The Testcontainers profile uses database Docker images from curated public images so the suite behaves predictably in CI and on a typical developer machine. There are situations where those default images are not enough: you may need to test against specific images for compliance or to align with versions you use in production. In those cases you can keep the same Maven targets and set of tests but specify alternate database images.

Do that with **JVM system properties** by adding `-Dtc.image.<database>=<image>:<tag>` on the maven command line when you run the tests. The `<database>` segment is `postgresql`, `mysql`, `mariadb`, `mssqlserver`, or `oracle`. Leave a property unset to keep that database’s default image; add as many `-D` pairs as you need in one invocation.

Here's an example of specifying a PostgreSQL 16 image:

```bash
./mvnw clean test -Psb4,tc -Dtc.image.postgresql=postgres:16-alpine
```

You can find the default image tags and the exact `tc.image.*` property keys in [`ContainerImages.java`](../src/test/containers/java/expresspecs/ContainerImages.java).

## Why Database-Specific Tests?

The H2 test suite provides fast feedback but masks real-world dialect issues because H2 silently
coerces type mismatches that strict databases reject outright. The Testcontainers suite runs the
**full specification test suite** against each database, catching problems like:

- SQL functions that only exist in some dialects (e.g., `year()`, `month()`, `day()` on PostgreSQL)
- JDBC parameter type mismatches that H2 accepts but PostgreSQL or Oracle rejects with an error
- Timezone and offset handling differences across database timestamp types

The `tc` profile (PostgreSQL, MySQL, MariaDB, SQL Server) is intended to run in CI on every push
alongside the H2 suite. Oracle is kept in a separate profile because the Docker image is ~2 GB
and takes 60–90 seconds to initialise; keeping is separate allows selective or parallel running of it.

On GitHub Actions, Docker is available out of the box on `ubuntu-latest` runners, no additional
setup is needed beyond activating the profile.

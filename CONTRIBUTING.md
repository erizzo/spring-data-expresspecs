# Implementation Notes

### Spring Boot 3 and 4 Compatibility

This library supports both **Spring Boot 3.5** and **Spring Boot 4.0** from a single codebase.

#### Why
The library's production code depends only on the JPA Criteria API and Spring Data JPA's `Specification` interface—APIs that are identical across both Spring Boot versions. No version-specific code is needed at runtime.

The challenge is in the **test infrastructure**. Spring Boot 4 relocated several key test classes (like `@DataJpaTest`) to new packages.

#### How
The project uses **Maven profiles** (`sb3` and `sb4`) with **separate test source trees** that share common base classes:

```
src/
├── main/java/                    # Production code — version-independent
├── test/java/                    # Common test code (base classes, entities, unit tests)
├── test-springboot3/java/        # SB3-specific: thin subclasses + config
├── test-springboot4/java/        # SB4-specific: thin subclasses + config
├── test-containers/java/         # Testcontainers tests (PostgreSQL, MySQL, MariaDB, SQL Server)
└── test-containers-oracle/java/  # Testcontainers test for Oracle (separate due to startup cost)
```

- **`sb4`** (default) — uses Spring Boot 4.0, adds `src/test-springboot4/java`
- **`sb3`** — uses Spring Boot 3.5, adds `src/test-springboot3/java`
- **`tc`** — adds Testcontainers dependencies and `src/test-containers/java`; **SB4 only** (see below)
- **`oracle`** — adds the Oracle Free Testcontainers module and `src/test-containers-oracle/java`; must be combined with `tc`; **SB4 only**

**Common test code** (`src/test/java`) contains abstract base test classes (e.g., `BaseJPAIntegrationTest`), entities, and an `EntityManagerWrapper`.
**Version-specific test code** contains only the Spring configurations and thin, empty subclasses of the base test classes.

#### Running the tests

```bash
# H2 in-memory (fast, no Docker required)
./mvnw clean test           # Spring Boot 4 (default)
./mvnw clean test -Psb3     # Spring Boot 3.5

# Testcontainers — real databases via Docker (requires Docker; SB4 only)
./mvnw clean test -Psb4,tc              # PostgreSQL, MySQL, MariaDB, SQL Server
./mvnw clean test -Psb4,tc,oracle      # adds Oracle Free (~60–90 s startup)
```

> **Note:** Explicitly passing `-Psb4` is required when also passing `-Ptc` or `-Poracle`,
> because specifying any `-P` flag deactivates the `sb4` profile's `activeByDefault` setting.

> **Testcontainers requires Spring Boot 4.** The `tc` and `oracle` profiles do not work with `-Psb3`.
> Spring Boot 3's BOM manages Testcontainers **1.x**; Spring Boot 4 manages Testcontainers **2.x**.
> The two lines use different Maven coordinates and **Java package names**, so `-Psb3,tc` (or
> `-Poracle` with `sb3`) will not compile or resolve correctly — always use `-Psb4,tc` (and pass
> `sb4` explicitly whenever you pass any other `-P`, since it is `activeByDefault`).

#### Why Testcontainers?

The H2 test suite provides fast feedback but masks real-world dialect issues because H2 silently
coerces type mismatches that strict databases reject outright. The Testcontainers suite runs the
**full specification test suite** against each database, catching problems like:

- SQL functions that only exist in some dialects (e.g. `year()`, `month()`, `day()` on PostgreSQL)
- JDBC parameter type mismatches that H2 accepts but PostgreSQL or Oracle rejects with an error
- Timezone and offset handling differences across database timestamp types

The `tc` profile (PostgreSQL, MySQL, MariaDB, SQL Server) is intended to run in CI on every push
alongside the H2 suite. Oracle is kept in a separate profile because the Docker image is ~2 GB
and takes 60–90 seconds to initialise; it is best suited to a nightly scheduled job.

On GitHub Actions, Docker is available out of the box on `ubuntu-latest` runners — no additional
setup is needed beyond activating the profile.

### Publishing a Release to Maven Central

1. **Update the version** in `pom.xml` — change `<version>` from `x.y.z-SNAPSHOT` to the release version (e.g. `0.1.0`). Commit and push.
2. **Create a GitHub Release** — in the GitHub UI, create a new release targeting that commit. Name the tag `v0.1.0` (matching the POM version) and publish it.
3. **CI publishes automatically** — the release workflow imports the GPG key, signs all artifacts, and deploys to Maven Central. Monitor progress in the Actions tab and at [central.sonatype.com](https://central.sonatype.com).
4. **Bump to next snapshot** — after the release is confirmed on Central, update `pom.xml` to the next development version (e.g. `0.2.0-SNAPSHOT`) and commit.

#### Prerequisites (one-time setup)

The following GitHub Actions secrets must be set on the repository:

| Secret | Description |
|---|---|
| `CENTRAL_USERNAME` | Token username from Central Portal → Account → Generate User Token |
| `CENTRAL_PASSWORD` | Token password (same place) |
| `GPG_PRIVATE_KEY` | ASCII-armored GPG private key (`gpg --export-secret-keys --armor <key-id>`) |
| `GPG_PASSPHRASE` | Passphrase for the GPG key |

The GPG public key must be uploaded to `keys.openpgp.org` so Central can verify signatures:
```bash
gpg --keyserver keys.openpgp.org --send-keys <key-id>
```

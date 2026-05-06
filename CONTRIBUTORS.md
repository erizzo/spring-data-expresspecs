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
├── main/java/                  # Production code — version-independent
├── test/java/                  # Common test code (base classes, entities, unit tests)
├── test-springboot3/java/      # SB3-specific: thin subclasses + config
└── test-springboot4/java/      # SB4-specific: thin subclasses + config
```

- **`sb4`** (default) — uses Spring Boot 4.0, adds `src/test-springboot4/java`
- **`sb3`** — uses Spring Boot 3.5, adds `src/test-springboot3/java`

**Common test code** (`src/test/java`) contains abstract base test classes (e.g., `BaseJPAIntegrationTest`), entities, and an `EntityManagerWrapper`.
**Version-specific test code** contains only the Spring configurations and thin, empty subclasses of the base test classes.

#### Running the tests

```bash
./mvnw test -Psb4   # Spring Boot 4 (default)
./mvnw test -Psb3   # Spring Boot 3.5
```

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

# Contributing

The production API is compatible with both Spring Boot 3.5 and 4; however the _tests_ use Maven profiles and separate
test source trees. For details about the profiles, directory layout, Testcontainers, and what runs in CI, see **[TESTING.md](TESTING.md)**.

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

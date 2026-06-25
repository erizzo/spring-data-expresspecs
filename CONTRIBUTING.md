# Contributing

Thanks for your interest in improving Spring Data ExpresSpecs! Contributions of all kinds are welcome: bug reports, documentation fixes, new Specification factory methods, and tests.

## Reporting issues

Please open a [GitHub issue](https://github.com/erizzo/spring-data-expresspecs/issues) for bugs or feature requests. For bugs, a minimal reproducing example (the domain entity, the Specification call, and the resulting or expected query) is the most helpful thing you can include.

## Prerequisites

- JDK 17 or later.
- No local Maven install is required; use the bundled wrapper (`./mvnw`).

## Building and testing

```bash
./mvnw clean install   # build
./mvnw clean test      # run the H2 tests for the default Spring Boot version only
```

The command above is a quick smoke test, **not** the full suite. The production API is compatible with both Spring Boot 3.5 and 4, and the tests use Maven profiles and separate source trees to cover both versions plus real databases via Testcontainers (the `sb3`, `sb4`, `tc`, and `oracle` profiles). A change is not adequately tested until it passes across all of them.

**Before opening a pull request you must run the complete test suite as described in [docs/testing.md](docs/testing.md).** That document is the authoritative reference for the profiles, directory layout, Testcontainers setup, and what CI runs; follow it rather than relying on the single command above.

## Submitting a pull request

1. Fork the repository and create a topic branch off `main`.
2. Make your change, keeping it focused, and add tests that cover it.
3. Run the **complete test suite** across all profiles (`sb3`, `sb4`, `tc`, and `oracle`) as described in [docs/testing.md](docs/testing.md), not just the default `./mvnw clean test`, and make sure every run is green.
4. Open a pull request describing the change and the motivation behind it.

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE), the same license that covers this project.

## Coding style

- This is a convenience library whose whole point is readable, intent-revealing code; favor expressiveness over terseness.
- Match the conventions already present in the surrounding code: naming, formatting, and idioms. Do not reformat existing code as part of an unrelated change.
- Indent with **tabs**, not spaces. Many editors default to spaces, so check your settings before committing to avoid re-indenting whole files.
- Prefer Lombok (`@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@Builder`, `@Data`, `@Value`, etc.) over hand-written constructors, getters, and setters. Keep explicit constructors when they hold real logic such as validation or defensive copies, and for `record` types use the language features rather than duplicating accessors.
- Follow the null/empty-safety convention: factory methods return `BasicSpecifications.unrestricted()` rather than `null` for absent inputs.

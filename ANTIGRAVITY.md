# Antigravity Project Instructions

This file contains specific rules and preferences for the `erizzo-spring-utils` project. These rules must be followed by Antigravity in all future sessions.

## 1. Indentation & Formatting
- **Default Indentation**: Always use **Tabs** for indentation in all new or modified code.
- **Strict Formatting Policy**: Never change the formatting of existing code (indentation, spacing, line breaks, etc.) unless explicitly instructed by the user.
- **Hybrid Files**: If you encounter a file that still uses spaces for indentation, you MUST ask the user whether to continue with spaces (to maintain file consistency) or switch to tabs (the project preference) before making any modifications.

## 2. Java Best Practices
- **Type Safety**: Avoid using raw JPA types. Always use generic parameters for `Path<?>`, `Specification<?>`, etc., to maintain type safety.
- **Association Logic**: When building Specifications for collections, prefer iterative `.value()` calls on the `CriteriaBuilder.In` predicate over passing a whole collection as a single value.

## 3. Testing
- Always verify changes across both Spring Boot profiles:
	- `./mvnw clean test -Psb3`
	- `./mvnw clean test -Psb4`

# Antigravity Project Instructions

This file contains specific rules and preferences for the `erizzo-spring-utils` project. These rules must be followed by Antigravity in all future sessions.

## 1. Indentation & Formatting
- **Default Indentation**: Always use **Tabs** for indentation in all new or modified code.
- **Strict Formatting Policy**: Never change the formatting of existing code (indentation, spacing, line breaks, etc.) unless explicitly instructed by the user.
- **Javadoc Integrity**: DO NOT change the line wrapping, indentation, or general structure of existing Javadoc comments. Terminology updates for technical accuracy are permitted, but they must be applied in-place without altering the surrounding formatting or line breaks.
- **Line Length Guideline**: Prefer longer lines (up to 120 characters) over aggressive wrapping. Avoid wrapping that leaves only 1 or 2 words isolated on a new line.
- **No Vertical Alignment**: Do not add extra spaces in Javadocs to vertically align text across multiple lines. Always use exactly one space after the `*` prefix (unless it's a deliberate structural indent like a nested list).
- **Redundant @return Tags**: Avoid including or adding `@return` tags in Javadoc if they only repeat what is already stated in the method description.
- **Hybrid Files**: If you encounter a file that still uses spaces for indentation, you MUST ask the user whether to continue with spaces (to maintain file consistency) or switch to tabs (the project preference) before making any modifications.

## 2. Java Best Practices
- **Type Safety**: Avoid using raw JPA types. Always use generic parameters for `Path<?>`, `Specification<?>`, etc., to maintain type safety.
- **Association Logic**: When building Specifications for collections, prefer iterative `.value()` calls on the `CriteriaBuilder.In` predicate over passing a whole collection as a single value.

## 3. Testing
- Always verify changes across both Spring Boot profiles:
	- `./mvnw clean test -Psb3`
	- `./mvnw clean test -Psb4`

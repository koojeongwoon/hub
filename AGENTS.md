# Repository Guidelines

## Project Structure & Module Organization
- Code lives under `src/main/java/com/tinyquest/hub`, grouped by domain (`auth`, `user`, `shared`); keep shared concerns only in `shared`.
- Configuration, SQL helpers, and static files stay in `src/main/resources`; profile overrides use `application-<profile>.yml` with `application-local.yml` for dev.
- Tests mirror main packages under `src/test/java`, with profile configs in `src/test/resources` (`application-h2.yml`, `application-tc.yml`).
- Root files `build.gradle.kts`, `compose.yaml`, and the `Dockerfile` drive the Gradle build, local MariaDB service, and container image.

## Build, Test, and Development Commands
- Start the API via `./gradlew bootRun -Dspring.profiles.active=local`; pair with `docker compose up mariadb` when the database is needed.
- `./gradlew clean build` runs compilation, checks, and produces the executable jar in `build/libs`.
- `./gradlew test` executes the JUnit 5 suite with H2; append `-Dspring.profiles.active=tc` to exercise the Testcontainers-backed MariaDB flow.
- `./gradlew bootJar` packages a production-style jar without the clean cycle.

## Coding Style & Naming Conventions
- Target Java 21, 4-space indentation, and UTF-8 sources. Prefer constructor injection (`@RequiredArgsConstructor`) and MapStruct for DTO mapping.
- Follow `com.tinyquest.hub.<domain>.<layer>` packages; expose cross-domain contracts through `shared.port` to respect Modulith boundaries.
- Name controllers with the `*RestController` suffix and DTOs with `Request`/`Response`; use UpperCamelCase for types and lowerCamelCase for members.
- Keep SQL snippets in `shared.utils` loaders and reusable constants under `shared.constants` instead of scattering literals.

## Testing Guidelines
- Reuse the MockMvc setup shown in `src/test/java/com/tinyquest/hub/user/api/controller`; authenticate via the existing `AuthPrincipal` helper.
- Extend `ModulithStructureTest` when introducing new published events or shared interfaces to confirm module rules.
- Use H2 (`@ActiveProfiles("h2")`) for fast unit-style runs and switch to Testcontainers (`@ActiveProfiles("tc")`) when verifying MariaDB behavior.
- Document intent with `@DisplayName` (Korean or English) and align method names with the HTTP or service behavior under test.

## Commit & Pull Request Guidelines
- Mirror the concise, action-oriented commit summaries already in history (e.g., `리스폰스 클래스 네이밍 변경`); lead with the domain/topic and skip ending punctuation.
- Break large stories into focused commits (schema change, service logic, controller surface) to simplify review.
- In pull requests, describe the problem, the solution, verification steps (`./gradlew test`, manual calls), and any required profile or config updates.
- Link issues when available and attach screenshots or sample JSON whenever REST contracts or response wrappers change.

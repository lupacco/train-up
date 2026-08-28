# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Also read **`AGENTS.md`**: short invariants shared with the Expo app (JWT ownership, session dates, breaking API changes). Do not duplicate that contract here.

## Project Overview

**train-up** is a Maven multi-module Spring Boot project for a gym diary app: the user writes a **plan** (workout, exercises, series, goal reps/load) and later logs what they **actually performed**. The calendar and load charts are derived from performed sessions, not from the plan.

- `customer` — the only live service (port **8085**): auth, workouts, icons, live/diary sessions, exercise progress
- `fraud` — stub module (`Main.java` only; graph “fraud” rules are deferred)

The mobile client lives in a sibling repo: `~/Desktop/personal-projects/devWPeter/fe-train-up` (Expo). It consumes this API; keep `GET /workout/me` and session ownership based on the JWT. Do not take `userId` from the request body.

## Build & Run Commands

```bash
# Build all modules
mvn clean install

# Build a single module
mvn clean install -pl customer

# Run the customer service
mvn spring-boot:run -pl customer

# Run tests for all modules
mvn test

# Run tests for a single module
mvn test -pl customer

# Run a single test class
mvn test -pl customer -Dtest=ClassName

# Run a single test method
mvn test -pl customer -Dtest=ClassName#methodName
```

Integration tests use Testcontainers (Postgres 16). `WorkoutSessionFlowTest` covers live sessions, backdated diary logs, catalog reuse, and ownership.

## Required Environment Variables

The `customer` service requires these env vars at startup:

```
DATABASE_URL=jdbc:postgresql://localhost:5432/train_up
DB_USERNAME=<db_user>
DB_PASSWORD=<db_password>
JWT_PUBLIC_KEY=file:$HOME/.trainup/app.pub
JWT_PRIVATE_KEY=file:$HOME/.trainup/app.key
```

`customer/src/main/resources/app.key` and `app.pub` are **empty placeholders**. Point `JWT_*` at a real RSA keypair kept outside git. Tests generate keys in memory and do not need these files.

JWTs expire in **3500 seconds** (~58 min). There is no refresh route.

## Architecture

### customer module

**Layering:** `Controller → Service → Repository → JPA Repository`

- **Controllers** (`controller/`), all under `/api/v1`:

  | Controller | Base path | Role |
  | --- | --- | --- |
  | `AuthController` | `/api/v1` | `POST /register`, `POST /authenticate`, `GET /test` (public) |
  | `CustomerUserController` | `/api/v1/customer` | `GET /token` (current user from Bearer), `GET /{username}` |
  | `WorkoutController` | `/api/v1/workout` | Create workout, add exercises, `GET /me`, list exercises, `POST /{id}/performed` (start/resume session), history |
  | `PerformedController` | `/api/v1/performed` | Day/month listing, session detail, finish, log a serie |
  | `IconController` | `/api/v1/icon` | `GET` catalog (Flyway seed V6) |
  | `ExerciseController` | `/api/v1/exercise` | `GET /{exerciseId}/progress` (load over time) |

- **Services** (`service/`): `AuthService` (register/authenticate), `WorkoutService` (plans), `WorkoutSessionService` (performed runs, calendar, progress, backfill), `CustomerUserService`, `CurrentUserService` (user from SecurityContext — never from a path/body id), `IconService`, `JwtService` (RSA encode/decode).
- **Repositories** — two layers:
  - `repository/jpa/` — Spring Data JPA interfaces (named `Jpa*Repository`)
  - `repository/ExerciseRepository` — a custom `@Repository` bean that wraps both `JdbcTemplate` (for joins with goal data) and JPA repos. This is the only place raw SQL lives.
- **Models** (`model/`): JPA entities. `WorkoutExercise` uses a composite `@EmbeddedId` (`WorkoutExerciseId`). `Workout` has two `@ManyToMany` relationships: `assignedUsers` (via `user_workout`) and `assignedExercises` (via `workout_exercise`).
- **DTOs** (`dto/request/`, `dto/response/`): Java records for API I/O.
- **Exception handling**: `APIException` is a base exception with a `buildErrorResponse()` method; domain exceptions extend it. `RestExceptionHandler` (`@ControllerAdvice`) catches these and `ConflictException`.

### Sessions (plan vs diary)

`POST /api/v1/workout/{id}/performed` accepts an optional body `{ "performedOn": "yyyy-MM-dd" }`:

- omitted or **today** → live session; resume the open one if it exists
- a **past** date → new diary session at `12:00` that day; never reuse a live open session
- a **future** date → 409

`PATCH /api/v1/performed/{id}/finish` keeps `endTime` on the same calendar day for backfills (`start + 1 min`) so the calendar and charts do not leak into “now”. Future days must not be logged; past days are allowed (the mobile app warns, the API does not block).

The logged user always comes from the JWT. Creating a workout does not take `userId` in the body.

### Security

Spring Security with OAuth2 Resource Server (JWT). Public endpoints: `/api/v1/authenticate`, `/api/v1/register`, `/api/v1/test`. All other endpoints require a Bearer JWT.

### Database

PostgreSQL with Flyway migrations (`customer/src/main/resources/db/migration/`). DDL is managed exclusively through Flyway (`ddl-auto: validate`). Key tables: `customer_user`, `workout`, `exercise`, `workout_exercise` (join table with extra columns: `series`, `rep_goals[]`, `weight_goals[]`), `user_workout`, `workout_performed`, `exercise_performed`, `icon`.

Column `exercise_performed.weigth_performed` is a historical typo; map it in JPA, do not rename without a migration the mobile client knows about.

### Logging convention

Every service/repository method logs `[start] ClassName - methodName` and `[finish] ClassName - methodName` at `DEBUG` level.

# AGENTS.md

Collaboration contract for any coding agent in this repo. Architecture, commands and endpoint tables live in `CLAUDE.md` (EN) / `CLAUDE-pt.md` (PT) — do not copy them here.

The mobile app is a **separate** repo: `~/Desktop/personal-projects/devWPeter/fe-train-up`. Frontend work happens there (Cursor). This repo owns Spring Boot + Postgres. Do not implement Expo screens here.

## Invariants — do not break without coordinating with the frontend

1. **Identity from the JWT only.** `CurrentUserService` / SecurityContext. Never take `userId` (or equivalent) from the body or query to decide ownership. `GET /api/v1/workout/me` stays; do not revive listing by path user id (IDOR).
2. **HTTP paths and JSON field names** are the contract the Expo app types in `fe-train-up/src/api/contract.ts` and `src/api/rest.ts`. Changing a path, a record field, or a status code is a breaking change — say so and expect a frontend follow-up. Prefer additive DTOs.
3. **Sessions:** `POST /api/v1/workout/{id}/performed` with optional `{ "performedOn": "yyyy-MM-dd" }`. Omit/today = live (resume open). Past = new diary row at `12:00` that day, never reuse the live open session. Future = **409**. Finish keeps backfills on the same calendar day.
4. **Past days are allowed** (forgotten gym log). The app warns; this API must not block them. Do not add “anti-fraud” graph rules in the `fraud` module unless the owner asks.
5. **JWT is 3500s and there is no refresh.** Do not silently change TTL or add refresh routes as a drive-by. A longer trial token is an explicit product decision.
6. **Flyway owns DDL** (`ddl-auto: validate`). The column `exercise_performed.weigth_performed` is a historical typo — map it, do not rename in place.
7. **Public routes stay** `/api/v1/authenticate`, `/api/v1/register`, `/api/v1/test`. Everything else Bearer.

## When you change the API

- Extend `WorkoutSessionFlowTest` (or add a focused test) for session/auth/ownership behaviour.
- Mention the frontend files that will need a matching change (`src/api/types.ts`, `rest.ts`, `mock.ts`).

## Out of scope here

Pixel-perfect Figma, forgot-password, native date picker, AWS/production deploy, and the Expo UI. Those belong to the other agent or a later decision.

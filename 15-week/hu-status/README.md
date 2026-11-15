<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       15-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 15

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: By_Sellens-Platform
- SPRINT_GOAL: Promote the Sales PostgreSQL setup and schema migrations to `main` so the Sales API can run against a reproducible database setup.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-XXX-001 | Promote the Sales PostgreSQL setup and schema migrations to `main` | done | [PR #4](https://github.com/code-corhuila/bysellens-sales-db/pull/4), [PR #6](https://github.com/code-corhuila/bysellens-sales-db/pull/6) |

## 2. My individual contribution

- I merged the Sales schema migrations into `main` through PR #4. The migrations define the sales and sale detail tables, enforce data constraints, and create the RabbitMQ outbox table with an index for pending messages.
- I merged the PostgreSQL setup into `main` through PR #6. The changes include Docker Compose, a PostgreSQL initialization script, setup documentation, and an `.env.example` with placeholder credentials. The `.gitignore` rules exclude local environment files.

## 3. Blockers and risks

- Both PRs are merged. A fresh setup still needs valid local environment variables, and the Sales API connection and outbox flow should be checked together.

## 4. Plan for next week

- Start PostgreSQL from a clean Docker Compose environment and verify that the migrations apply successfully.
- Connect the Sales API, create a test sale, and verify that its event is written to the outbox and published to RabbitMQ.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [ ] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

- [Commit 566bdb1 — Merge Sales schema migrations into main](https://github.com/code-corhuila/bysellens-sales-db/commit/566bdb139c049c1193234d0cba30081ab8ea9a15)
- [Commit 2ea0486 — Merge Sales PostgreSQL setup into main](https://github.com/code-corhuila/bysellens-sales-db/commit/2ea0486e67211239a919664bb5fa55a70bc38bb3)

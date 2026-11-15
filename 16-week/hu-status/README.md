<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       16-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 16

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: By_Sellens-Platform
- SPRINT_GOAL: Tag version 3.0.0 of the Sales database and its API documentation so the team can reference both releases.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-XXX-001 | Tag the Sales database and API documentation releases for v3.0.0 | done | [Sales schema migrations PR #4](https://github.com/code-corhuila/bysellens-sales-db/pull/4), [PostgreSQL setup PR #6](https://github.com/code-corhuila/bysellens-sales-db/pull/6) |

## 2. My individual contribution

- I created the `v3.0.0` tag in `bysellens-sales-db` to mark the Sales database release.
- I created the `docs-07-api-v3.0.0` tag in `bysellens-docs` to mark the corresponding version of the API documentation.

## 3. Blockers and risks

- The main release risk is a mismatch between the tagged API documentation and the tagged database setup.

## 4. Plan for next week

- Start PostgreSQL from the `v3.0.0` tag and verify that the migrations apply to a clean database.
- Run the documented Sales API examples against that setup and record any discrepancies.

## 5. Compliance self-check

- [ ] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [ ] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [ ] No secrets; config via environment variables

## 6. Evidence links

- [Sales database release v3.0.0](https://github.com/code-corhuila/bysellens-sales-db/releases/tag/v3.0.0)
- [API documentation release docs-07-api-v3.0.0](https://github.com/code-corhuila/bysellens-docs/releases/tag/docs-07-api-v3.0.0)

# Weekly Status - Week 09

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->

* FULL_NAME: Seam Yong Agudelo
* GITHUB_USER: seamyong17
* TEAM: By_Sellens
* SPRINT_GOAL: Align authentication, stock, and product API contracts

<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID      | Title                                                  | Status (todo/doing/done) | Evidence (PR or commit URL)                                                                     |
| ---------- | ------------------------------------------------------ | ------------------------ | ----------------------------------------------------------------------------------------------- |
| HU-XXX-001 | Align authentication, stock, and product API contracts | done                     | https://github.com/code-corhuila/bysellens-docs/commit/92b713347bddfc120b123c9de8caefe0196b5a42 |

## 2. My individual contribution

* Updated and aligned the OpenAPI contracts for authentication, stock, and product services.
* Documented the stock concurrency decision through ADR-003, including optimistic concurrency and version handling.
* Updated the API documentation to keep the contracts consistent across the affected services.
* Contributed these changes through Pull Request #45.

## 3. Blockers and risks

* No major blockers were encountered during the documentation work.
* A potential risk is keeping the API contracts synchronized with the future service implementation and migration.

## 4. Plan for next week

* Continue validating the API contracts with the service implementations.
* Review pending inconsistencies in the OpenAPI documentation.
* Support the implementation and testing of the documented authentication and stock behaviors.

## 5. Compliance self-check

* [ ] Conventional Commits - `type(scope): summary`
* [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
* [x] Testable acceptance criteria
* [ ] Tests added/updated (unit / integration)
* [x] DDD / hexagonal boundaries respected (domain has no I/O)
* [x] No secrets; config via environment variables

## 6. Evidence links

* https://github.com/code-corhuila/bysellens-docs/commit/92b713347bddfc120b123c9de8caefe0196b5a42
* https://github.com/code-corhuila/bysellens-docs/pull/45

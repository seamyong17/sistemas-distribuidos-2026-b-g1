<!-- HU-STATUS TEMPLATE - Keep the comment markers and table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
     14-week/hu-status/README.md (inside YOUR fork). English. -->

# Weekly Status - Week 14

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: By_Sellens
- SPRINT_GOAL: PENDING — copy the exact value from the profile CONFIG
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| PENDING-HU-ID | Register a sale and coordinate inventory updates | doing | [PR #5](https://github.com/code-corhuila/bysellens-sales-api/pull/5) |

## 2. My individual contribution
- I prepared `bysellens-sales-api` to build and run independently by adding a root Maven project, a Dockerfile, and the shared support classes used by the service.
- I integrated the Sales controller, service, sales and line-item entities, repository, and initial SQL migration into the standalone repository. I also included the existing inventory coordination logic.
- I preserved the Sales controller and service regression tests, added `VALIDATION.md`, and updated `README.md` with instructions to build, test, and run the service.
- I merged PRs #4, #5, and #6 into `main` on November 10, 2026.

## 3. Blockers and risks
- End-to-end validation of sale registration and the resulting inventory update requires the database and inventory integration to be available in the same test environment.

## 4. Plan for next week
- Run `mvn clean verify` from a clean checkout and record the results.
- Verify that registering a sale persists the sale and its line items and triggers the expected inventory update.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [ ] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [ ] No secrets; config via environment variables

## 6. Evidence links
- [PR #4 — Standalone Maven build and runtime support](https://github.com/code-corhuila/bysellens-sales-api/pull/4)
- [PR #5 — Sales API, persistence, and inventory coordination](https://github.com/code-corhuila/bysellens-sales-api/pull/5)
- [PR #6 — Regression tests and validation documentation](https://github.com/code-corhuila/bysellens-sales-api/pull/6)

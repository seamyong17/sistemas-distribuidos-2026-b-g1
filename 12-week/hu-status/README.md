<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       12-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 12

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: Bysellens-Platform
- SPRINT_GOAL: Align By_Sellens API, UI/UX and DevOps documentation with the current second-cut scope and accepted architectural decisions.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| N/A — documentation task | Add REST guidelines and the OpenAPI contract registry | done | [PR #77](https://github.com/code-corhuila/bysellens-docs/pull/77), [merge commit](https://github.com/code-corhuila/bysellens-docs/commit/88fba09073ef8844a90ed60510cdf2d74397b9db) |
| N/A — documentation task | Align UI design and navigation with the current project scope | done | [PR #79](https://github.com/code-corhuila/bysellens-docs/pull/79), [merge commit](https://github.com/code-corhuila/bysellens-docs/commit/c02e6c7c76660e3445f11e8890cd2d44cbf68fd3) |
| N/A — documentation task | Add project-specific delivery and release guidance | done | [PR #80](https://github.com/code-corhuila/bysellens-docs/pull/80), [merge commit](https://github.com/code-corhuila/bysellens-docs/commit/a800c700c7fa7e3a21bbe7a92d1ea9b4bbbabf97) |

## 2. My individual contribution
- Added `07-api/guidelines.md` to document service boundaries, REST paths, authentication, authorization, errors, pagination and version-controlled stock updates.
- Added `07-api/contracts/openapi/README.md` to index the existing service contracts and distinguish accepted design from pending runtime verification.
- Updated the three documents in `12-ux-ui`: `README.md`, `design-system.md` and `navigation-map.md`.
- Aligned navigation with ADMIN login, protected business modules, local logout and the current API scope.
- Clarified absolute stock updates, `expectedVersion` and HTTP 409 conflict handling.
- Distinguished historical mockup proposals from features supported by the current requirements and contracts.
- Updated `10-devops/README.md`, `environments.md` and `local-setup.md` to reflect the selected project stack and configuration boundaries.
- Added `10-devops/ci-cd.md` and `release-checklist.md` to define delivery evidence, quality gates and release-readiness checks.
- Contributed through the documentation branches `docs/openapi-contracts`, `docs/ux-ui-guidelines` and `docs/devops-guides`.
- The three documentation changes were integrated into `main` through PRs #77, #79 and #80. These merge records establish documentation integration, not completion of the related application features.

## 3. Blockers and risks
- Backend implementation, integrated deployment and runtime acceptance are not established by these documentation changes.
- Authentication and authorization require verification against the real services; documented RS256 and ADMIN requirements do not prove enforcement.
- Reliable event publication, asynchronous sale completion and recovery still require implementation decisions and tests.
- Mockup features and workshop prototypes must not be mistaken for implemented production functionality.
- Actual CI execution, security scans, performance measurements and recovery rehearsals remain separate verification tasks.
- These documentation tasks have no confirmed functional HU identifiers; none were invented for this report.

## 4. Plan for next week
- Continue the security review of the React-to-Sales boundary and connect identified risks to explicit acceptance criteria.
- Review the documentation with teammates and preserve individual and shared contribution attribution.
- Identify actual implementation revisions and collect evidence for authentication, stock concurrency and asynchronous processing.
- Verify that future API and UI changes remain consistent with the accepted architecture and data ownership.
- Update documentation and weekly status when implementation or execution evidence becomes available.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

The work used documentation branches and PRs targeting the documentation repository's `main` branch. It did not use the code repository's per-environment HU workflow. Testable behavior was documented, but no application unit/integration tests or runtime domain-dependency checks are claimed by these three changes. Configuration guidance excludes real secret values.

## 6. Evidence links
- API documentation PR #77:
  https://github.com/code-corhuila/bysellens-docs/pull/77
- API documentation merge commit:
  https://github.com/code-corhuila/bysellens-docs/commit/88fba09073ef8844a90ed60510cdf2d74397b9db
- UI/UX documentation PR #79:
  https://github.com/code-corhuila/bysellens-docs/pull/79
- UI/UX documentation merge commit:
  https://github.com/code-corhuila/bysellens-docs/commit/c02e6c7c76660e3445f11e8890cd2d44cbf68fd3
- DevOps documentation PR #80:
  https://github.com/code-corhuila/bysellens-docs/pull/80
- DevOps documentation merge commit:
  https://github.com/code-corhuila/bysellens-docs/commit/a800c700c7fa7e3a21bbe7a92d1ea9b4bbbabf97 

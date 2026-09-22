# Weekly Status - Week 06

- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: By_Sellens
- SPRINT_GOAL: Define and document the API contracts for the By_Sellens microservices.

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-API-001 | Define API contracts for By_Sellens microservices | done | https://github.com/code-corhuila/bysellens-docs/commit/dcc3e2f8a68f4a7d325bd59096aa0b88a9cc4069 |

## 2. My individual contribution

- Updated the `07-api` documentation for the By_Sellens project.
- Defined API contracts using OpenAPI 3.0.3.
- Documented the Product, Inventory, Customer, and Sales services.
- Updated the shared OpenAPI components used by the services.
- Documented the proposed API Gateway routing.
- Defined API guidelines and common structures for requests, responses, pagination, and errors.
- Organized the API documentation so that the frontend and backend can use the same contracts as a reference.

## 3. Blockers and risks

- The `main` branch is protected and changes must be integrated through a Pull Request.
- API contracts must remain consistent with the domain, requirements, and data models.
- Changes made by other team members to requirements or domain definitions may require future updates to the API contracts.

## 4. Plan for next week

- Review the API contracts after the team's changes are integrated.
- Continue with the documentation required for the next project sections.
- Verify consistency between API contracts, microservices, and user stories.
- Support the development of sections 10, 11, and 13.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

- API contracts commit:
  https://github.com/code-corhuila/bysellens-docs/commit/dcc3e2f8a68f4a7d325bd59096aa0b88a9cc4069

<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       07-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 07

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: By_Sellens
- SPRINT_GOAL: Improve project documentation consistency and define the second-cut API, domain, data, context, and security specifications.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-DOC-001 | Update security documentation | done | https://github.com/code-corhuila/bysellens-docs/commit/f7127e342c85bba66836c6b2f8abd84df8294856 |
| HU-DOC-002 | Update project context and scope | done | https://github.com/code-corhuila/bysellens-docs/commit/ad061ccee0a3fc7e324b895586dc2ccb0657ca95 |
| HU-DOM-001 | Update domain rules and events | done | https://github.com/code-corhuila/bysellens-docs/commit/8268e42713665e692d813710685d17cb031fba24 |
| HU-DATA-001 | Update service data models | done | https://github.com/code-corhuila/bysellens-docs/commit/708e3fd35f2f2151f97e1457ba9c6176116382e8 |
| HU-API-001 | Update API contracts and authentication | done | https://github.com/code-corhuila/bysellens-docs/commit/bdd77ff2a233152d8973144e99a3d1f58efe93ea |

## 2. My individual contribution

- Updated the security policy and security rules for the By_Sellens project.
- Reviewed and updated the project context, scope, and glossary.
- Improved the domain documentation, including domain rules, events, and the domain map.
- Updated the service data models and clarified data ownership between microservices.
- Updated the API contracts using OpenAPI 3.0.3.
- Updated the Product, Inventory, Customer, and Sales service contracts.
- Improved the API authentication documentation and shared API components.
- Aligned the API contracts with the current domain and data model definitions.

## 3. Blockers and risks

- The `main` branch is protected and changes must be integrated through the required Pull Request workflow.
- Changes in domain rules or data models may require corresponding updates to the API contracts.
- Documentation between different project sections must remain consistent as the project evolves.

## 4. Plan for next week

- Continue reviewing consistency between domain, data, API, and microservice documentation.
- Work on the next required project sections.
- Review the microservice documentation and service responsibilities.
- Validate that the API contracts remain aligned with the project's functional requirements.
- Prepare the next project documentation updates and evidence.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

- Security documentation:
  https://github.com/code-corhuila/bysellens-docs/commit/f7127e342c85bba66836c6b2f8abd84df8294856

- Project context and scope:
  https://github.com/code-corhuila/bysellens-docs/commit/ad061ccee0a3fc7e324b895586dc2ccb0657ca95

- Domain rules and events:
  https://github.com/code-corhuila/bysellens-docs/commit/8268e42713665e692d813710685d17cb031fba24

- Service data models:
  https://github.com/code-corhuila/bysellens-docs/commit/708e3fd35f2f2151f97e1457ba9c6176116382e8

- API contracts and authentication:
  https://github.com/code-corhuila/bysellens-docs/commit/bdd77ff2a233152d8973144e99a3d1f58efe93ea

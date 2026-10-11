# Weekly Status - Week 10

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: BySellens
- SPRINT_GOAL: Implement and improve the Sales Portal, including reusable sales components, sales history, form validation, and automated tests.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-XXX-001 | Sales Portal implementation and validation | done* | [d894b69](https://github.com/code-corhuila/bysellens-sales-portal/commit/d894b691bcf07ec4a23dd12e062b3c252345d1b4), [3d7632d](https://github.com/code-corhuila/bysellens-sales-portal/commit/3d7632d257372c136cc14c965d172cf14dff1c51) |
| HU-XXX-002 | New sale form and reusable form components | done* | [bf79293](https://github.com/code-corhuila/bysellens-sales-portal/commit/bf79293ccca450b304722cf68491ba9ddd8b2c8d), [cfb944b](https://github.com/code-corhuila/bysellens-sales-portal/commit/cfb944b95ec05cdacfd10ed871be905783bdf972), [f5dc3ff](https://github.com/code-corhuila/bysellens-sales-portal/commit/f5dc3ffe47153c1784d281642dc1ad2b5945dee6) |
| HU-XXX-003 | Sales history, services, summary, and shared frontend setup | done* | [c41f651](https://github.com/code-corhuila/bysellens-sales-portal/commit/c41f65124400999bfb009022bcdd64d37de9cb69), [8418947](https://github.com/code-corhuila/bysellens-sales-portal/commit/84189476eadc4e21056310735fe89570cd07dd31), [b8915b2](https://github.com/code-corhuila/bysellens-sales-portal/commit/b8915b2f8d471811d1760a88015f7e834540ecde) |

## 2. My individual contribution
- Contributed to the Sales Portal implementation and integration using React and TypeScript.
- Extracted sales page logic into a reusable hook and implemented validation for customers, products, quantities, stock, and payment methods.
- Developed reusable components for the new sale form, form fields, sales history, and sales summary.
- Added or updated automated tests for sales logic, components, and service contracts.
- Integrated mock catalog and sales services and documented implementation increments.

## 3. Blockers and risks
- Ensure that the sales frontend remains compatible with the existing service contracts.
- Verify that all acceptance criteria and automated tests pass before considering the user stories complete.

## 4. Plan for next week
- Run the relevant unit and integration tests.
- Review the sales workflow and fix any outstanding issues.
- Verify the acceptance criteria and integration with the target environment.
- Update the documentation and evidence for the assigned user stories.

## 5. Compliance self-check
- [ ] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- https://github.com/code-corhuila/bysellens-sales-portal/commit/d894b691bcf07ec4a23dd12e062b3c252345d1b4
- https://github.com/code-corhuila/bysellens-sales-portal/commit/3d7632d257372c136cc14c965d172cf14dff1c51
- https://github.com/code-corhuila/bysellens-sales-portal/commit/bf79293ccca450b304722cf68491ba9ddd8b2c8d
- https://github.com/code-corhuila/bysellens-sales-portal/commit/cfb944b95ec05cdacfd10ed871be905783bdf972
- https://github.com/code-corhuila/bysellens-sales-portal/commit/f5dc3ffe47153c1784d281642dc1ad2b5945dee6
- https://github.com/code-corhuila/bysellens-sales-portal/commit/c41f65124400999bfb009022bcdd64d37de9cb69
- https://github.com/code-corhuila/bysellens-sales-portal/commit/84189476eadc4e21056310735fe89570cd07dd31
- https://github.com/code-corhuila/bysellens-sales-portal/commit/b8915b2f8d471811d1760a88015f7e834540ecde

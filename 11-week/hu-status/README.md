<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       11-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 11

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: Bysellens-Platform
- SPRINT_GOAL: Specify and prototype observability for the proposed Sales journey in By_Sellens.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-SALE-001 | Sales registration: observability prototype; business implementation remains pending | doing | [CP1 journey](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/3883199c92356c6860d91586a4837ba2204b6c98), [CP3 evidence](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/0a25750d858c9cdea0f714510216bbd08a6d8617) |

## 2. My individual contribution
- Prepared the CP0 workshop configuration and linked the personal reflection document.
- Mapped the proposed sales-service journey, including Sale, SaleItem, customer validation and Product-owned Stock, with trace context across HTTP and RabbitMQ design boundaries.
- Defined the CP2 structured log contract, domain fields, sensitive-data exclusions and seven explicitly synthetic log examples.
- Built a standalone Java/Spring Boot sales-service instrumentation prototype with trace-context handling and allowlisted JSON logs.
- Propagated traceparent to a local Product test double and exposed a request counter and latency histogram on /metrics.
- Executed 10 automated tests successfully: context handling, log fields, HTTP propagation, stock rejection, timeout, dependency failures, invalid input and metrics.
- Ran the prototype and Product stub locally; recorded actual HTTP, log and metric output in HOW-TO-RUN.md. No Sale was persisted.
- Specified the CP4 golden signals, PromQL and cardinality for Sales and its documented dependencies. Added and verified a 10-second histogram bucket for the selected design expectation.

## 3. Blockers and risks
- The supplied Sales portal snapshot has MOCK login and a temporary screen; the backend and real sale-registration integration are not implemented.
- Team documents contain conflicting stock/authentication descriptions. The workshop follows the confirmed Sales-specific contracts: Product owns Stock; Inventory owns Product data.
- Asynchronous sale completion, event publication recovery and multi-item consistency remain unresolved in the team design.
- Saturation and dependency/consumer metrics are proposed. No production Prometheus scraping, dashboard or live PromQL evaluation has been verified.
- Workshop changes were published directly to the course fork's main branch with student authorization; environment HU branches and PRs were not used for this work.
- This status records repository evidence. Moodle submission and acceptance have not been verified.

## 4. Plan for next week
- Continue CP6-CP10 in order: SLOs, error budgets, burn-rate rules, incident runbook, simulated incident and final technical status.
- Keep the future real Sales backend separate from the workshop prototype; integrate instrumentation only after business contracts and security are implemented.
- Resolve team documentation conflicts and validate real-user latency expectations with measurements when the backend exists.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- [Team documentation](https://github.com/code-corhuila/bysellens-docs): reviewed from the supplied archive; service/domain references are listed in each workshop file.
- [Workshop folder](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/tree/main/11-week/workshop).
- [CP0 setup](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/c5d55fa30794d7b47a9746e059da7ee17e28c133).
- [CP1 journey and traces](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/3883199c92356c6860d91586a4837ba2204b6c98).
- [CP2 log contract](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/ae01df1817fc27eea783f207d2f74c327114c9c4).
- [CP3 context and logger](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/8c724afc919cf8552a32cdacc19fe04b2193116d).
- [CP3 initial tests and execution](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/5bf63e32a3d5174fd45a74c343a7a3a2d59c9644).
- [CP3 propagation and metrics](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/9c397ec7f27d5353f181abb17afd5789fe82d9e0).
- [CP3 HTTP tests](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/46e3ed611fe66d84c4a8e3e0a55e335ebfc4ceea).
- [CP3 run commands and console evidence](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/0a25750d858c9cdea0f714510216bbd08a6d8617).
- [CP4 golden signals and metric catalog](https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/de973ec1b30853f74187e213e08d7e4f4da8a2f6).
- [Personal reflection document](https://docs.google.com/document/d/1frtao2jfFsyZTddwNuKRS81WsBYSRTWD22xksFyelyo).

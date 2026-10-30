# Week 13 - By_Sellens threat modeling workshop

Status: CP0 technical setup prepared. Boundary claimed in issue #49; reflection document created and shared. Personal reflection entries remain pending.

<!-- CONFIG-START -->
- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: Bysellens-Platform
- DOCS_REPO: https://github.com/code-corhuila/bysellens-docs
- BOUNDARY: React Sales microfrontend to sales-service, inbound GET and POST /api/v1/sales
- CLAIM_ISSUE: https://github.com/code-corhuila/bysellens-docs/issues/49
- REFLECTION_DOC: https://docs.google.com/document/d/1NEYSAGOchYLtFl8E6QszrjmZjbS1EPK-av2XXrCXBcY/edit
- LIBRARY_BOOK: Ramos Varón, Antonio Ángel; Barbero Muñoz, Carlos A.; Martínez Sánchez, Rubén; García Moreno, Ángel; González Nava, Jesús María. Hacking y seguridad de páginas Web. Bogotá: Ediciones de la U / Ra-ma, 2015. 1st ed. per publisher catalog. Call number 005.8 R175h, copy 1. Consulted physical pages: 15–18, chapter 1, section 1.1; owner supplied photographs. Pages 16 and 18 support privilege-oriented testing and documenting results; applied to E1 / story #53.
- LIBRARY_RECORD_TIME: 19:30 (reported by the student; record date and library evidence to confirm)
- STRIDE_PR: https://github.com/code-corhuila/bysellens-docs/pull/50
- STORIES: https://github.com/code-corhuila/bysellens-docs/issues/51 ; https://github.com/code-corhuila/bysellens-docs/issues/53 ; https://github.com/code-corhuila/bysellens-docs/issues/55
<!-- CONFIG-END -->

## Project context

The React Sales microfrontend calls sales-service to register Sale records and
read sales history. Sale contains SaleItem details, customerId, paymentMethod
and a server-calculated total. Sales validates customer/product data through
customer-service and product-service, persists sales and sale_items in sales_db,
and publishes SaleRegistered through RabbitMQ for asynchronous stock processing.

This continues the Sales journey documented in my week 11 workshop. That work
uses a workshop prototype; it does not establish a production Sales backend.

The claimed boundary covers inbound GET and POST /api/v1/sales from React.
Sales-to-database, outbound Customer/Product REST calls and RabbitMQ event
publication/consumption are adjacent boundaries, outside this individual claim.

## Sources in the team documentation

- 05-architecture/overview.md: services, data stores and communication.
- 07-api/contracts/openapi/sales-service.yaml: Sales operations and schemas.
- 07-api/authentication.md: RS256 JWT and ADMIN authorization requirements.
- 02-domain/domain-events.md: SaleRegistered and StockUpdated.

Authentication is an accepted design with implementation/runtime verification
pending. A documented control is not evidence that the running service enforces it.

## Library reference evidence

The student's spine photograph confirms the title, author names and call number
005.8 R175h, copy 1. Publication in Bogota in 2015 and a library record at 19:30
were reported by the student. The publisher catalog lists the first edition.
The owner supplied physical-page photographs of pp. 15–18. Pages 16 and 18 support privilege-oriented testing and documenting observed results.
No threat or acceptance criterion is attributed to the book before that passage
is identified. The photograph does not independently verify the library log.

## Checkpoint status

- CP0: claim issue and reflection document created; personal CP0 entry pending.
- CP1: attack surface, asset inventory and entry points prepared; runtime checks and personal reflection pending.
- CP2: six STRIDE threats, ranking and proposed residual risk published in PR #50; book citation grounded in pp. 16 and 18; owner risk acceptance pending.
- CP3: three security issues created with priorities and placed in team Backlog; [B] criterion added to #53; all three stories refined for CP7.
- CP4: isolated authorization module tested (6 passing cases) and workflow YAML parsed; proposal published after 20:10; personal reflection pending.
- CP5: status and evidence links prepared; teammate review published on PR #64 (2026-10-30, late); Moodle submission unverified.
- CP6: two peer comments addressed in STRIDE commit 8c781b1 and PR reply; owner acceptance pending.
- CP7: same three security stories refined; implementation remains open.
- CP8: new six-test pass recorded on 2026-10-29 in 04-security-ci/CP8-test-evidence.md; YAML parsed; workflow remains a proposal.
- CP9: personal implementation plan pending. Four-push/two-day rhythm requires separate verification.
- Reflection entries and implementation plan: pending personal writing by the student.

The local fork matched origin/main when fetched. Parent-repository synchronization
was not independently verified. Each technical checkpoint is committed and pushed
separately; pending personal work is not recorded as completed.

Expanded CP8 evidence (2026-10-30): eight test methods passed with zero failures,
errors or skips, including unsigned/malformed JWTs and a trusted-key RS512
rejection. The original six-test result is historical. See
04-security-ci/CP8-test-evidence.md in the workshop folder. Personal reflections,
CP9 and owner risk acceptance remain pending; no production integration or
live audit execution is claimed.

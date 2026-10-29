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
- LIBRARY_BOOK: Ramos Varón, Antonio Ángel; Barbero Muñoz, Carlos A.; Martínez Sánchez, Rubén; García Moreno, Ángel; González Nava, Jesús María. Hacking y seguridad de páginas Web. Bogotá: Ediciones de la U / Ra-ma, 2015. 1st ed. per publisher catalog. Call number 005.8 R175h, copy 1. Consulted pages pending.
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
The exact consulted pages and the security passage used remain pending.
No threat or acceptance criterion is attributed to the book before that passage
is identified. The photograph does not independently verify the library log.

## Checkpoint status

- CP0: claim issue and reflection document created; personal CP0 entry pending.
- CP1: attack surface, asset inventory and entry points prepared; runtime checks and personal reflection pending.
- CP2: six STRIDE threats, ranking and proposed residual risk published in PR #50; book passage and owner risk acceptance pending.
- CP3: three security issues created with priorities and placed in team Backlog; book-backed criterion pending.
- CP4: isolated authorization module tested (6 passing cases) and workflow YAML parsed; proposal published after 20:10; personal reflection pending.
- CP5: status and evidence links prepared; teammate review and Moodle submission pending.
- CP6-CP9: pending take-home work.
- Reflection entries and implementation plan: pending personal writing by the student.

The local fork matched origin/main when fetched. Parent-repository synchronization
was not independently verified. Each technical checkpoint is committed and pushed
separately; pending personal work is not recorded as completed.

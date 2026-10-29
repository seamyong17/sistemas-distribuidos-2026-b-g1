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
- LIBRARY_BOOK: Hacking y seguridad de paginas web (title reported by student); Bogota, 2015; exact edition, publisher, author spelling, call number and pages pending verification below
- LIBRARY_RECORD_TIME: 19:30 (reported by the student; record date and library evidence to confirm)
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

## Library information to verify against the physical book

The student reports consulting a library book titled "Hacking y seguridad de
paginas web" and having a library record. Authors were dictated as Antonio Angel
Ramos Baron; Carlos A. Punto Barbero Munoz; Ruben Martinez Sanchez; Angel Garcia
Moreno; Jesus Maria Gonzalez Navarro. This is an unverified transcription, not a
completed bibliographic reference. The student reports publication in Bogota in 2015 and a library record at 19:30. Confirm spelling, exact title, edition, publisher,
call number, pages used and the visit/loan record date against the book/record.
No threat or acceptance criterion is attributed to the book before those pages
and their relevant content have been identified.

## Checkpoint status

- CP0: claim issue and reflection document created; personal CP0 entry pending.
- CP1-CP9: pending.
- Reflection entries and implementation plan: pending personal writing by the student.

The local fork matched origin/main when fetched. Parent-repository synchronization
was not independently verified. Each technical checkpoint is committed and pushed
separately; pending personal work is not recorded as completed.

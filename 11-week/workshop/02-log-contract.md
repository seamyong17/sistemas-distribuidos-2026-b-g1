# CP2 - Structured log contract

## Scope and sources

This is a proposed JSON logging contract for the CP1 sale-registration journey.
No backend logger is implemented yet. All seven examples below are synthetic
workshop fixtures, not captured logs, verified timings or deployed configuration.
Field names, event names and log-level rules below are workshop proposals.

Team-document paths refer to the supplied bysellens-docs-main archive:

- `09-microservices/services/06-sales-service/README.md`: sales-service owns Sale
  and SaleItem; registration requires customer, product and stock validation.
- `07-api/contracts/openapi/sales-service.yaml`: POST /api/v1/sales,
  customerId, items, quantity, unitPrice and paymentMethod; unavailable validation
  and insufficient stock prevent registration.
- `07-api/contracts/openapi/product-service.yaml`: Product owns Stock and exposes
  GET /api/v1/products/{id}/stock.
- `09-microservices/services/06-sales-service/events.md`: SaleRegistered and
  StockUpdated are planned events; Bearer tokens must never enter messages.

## Required common fields

Every journey log is one JSON object on one line. Required means present and
non-null for request-scoped logs; unrelated startup logs are outside this contract.

| Field | JSON type | Required | Example | Purpose |
|---|---|---|---|---|
| timestamp | string | Yes | 2026-10-21T00:00:00.000Z | ISO-8601 UTC event time |
| level | string enum | Yes | INFO | ERROR, WARN, INFO or DEBUG severity |
| service | string | Yes | sales-service | Emitting component, not the downstream destination |
| version | string | Yes | workshop-prototype | Actual build identifier; sample is not a deployed release |
| environment | string | Yes | workshop | Separate prototype logs from real environments |
| trace_id | string | Yes | 91c786ab12de45ff8076aabd34cda109 | 32 lowercase hex digits, not all zero; joins spans |
| span_id | string | Yes | a100000000000001 | 16 lowercase hex digits, not all zero; active emitting span |
| correlation_id | string | Yes | w11-sale-attempt-001 | Opaque request/workflow ID; never an email or phone |
| event | string | Yes | sale.request.received | Stable machine-readable action name |
| message | string | Yes | Sale registration requested | Fixed human-readable summary without raw input |
| duration_ms | number | Yes | 3000 | Non-negative elapsed milliseconds; 0 at request receipt |

trace_id comes from validated incoming context or a new context. span_id changes
for child operations; it is not copied unchanged across the entire journey.
correlation_id is independent of trace_id and is not proof of HTTP idempotency.
Use a monotonic timer for durations and UTC wall-clock time for timestamp.

## Domain and operation fields

Use snake_case in logs and map from the source contract's camelCase properties.
Optional fields are omitted when unavailable; never fabricate an identifier.

| Field | JSON type | Required | Example | Purpose |
|---|---|---|---|---|
| sale_id | string | No | 850e8400-e29b-41d4-a716-446655440000 | Sale.id after persistence; absent for this rejected attempt |
| product_id | string | No | 550e8400-e29b-41d4-a716-446655440000 | Product reference for an item-level validation |
| quantity | integer | No | 2 | Requested SaleItem quantity, strictly positive |
| item_count | integer | No | 2 | Number of submitted sale lines; bounded diagnostic summary |
| payment_method | string enum | No | CASH | CASH, NEQUI, BANK_TRANSFER or OTHER; never payment credentials |
| stock_available | integer | No | 1 | Observed non-negative stock; not a reservation |
| route | string | No | /api/v1/sales | Route template, without query strings or concrete IDs |
| method | string | No | POST | HTTP operation |
| downstream_service | string | No | product-service | Destination component for a dependency operation |
| http_status | integer | No | 503 | Actual response status when available |
| error_code | string | No | STOCK_LOOKUP_TIMEOUT | Allowlisted diagnostic code; not an exception dump |
| rule | string | No | INSUFFICIENT_STOCK | Domain rule that rejected the attempt |

The first six rows are domain fields. Customer identifiers are deliberately
excluded from ordinary logs because they can link activity to personal records.
Prices, totals, complete item arrays and event payloads are also excluded.

## Never log and redaction policy

Apply an allowlist before serialization, including nested data. Do not serialize
request/response bodies, arbitrary headers, URLs, exceptions or ORM entities.

| Sensitive data | Treatment before logging |
|---|---|
| Customer name, email, phone, address and customerId | Drop fields; replace unavoidable diagnostic occurrences with [REDACTED_CUSTOMER] |
| Passwords, Bearer JWTs, cookies, session and refresh tokens | Drop headers/fields entirely; sanitize unavoidable occurrences to [REDACTED_SECRET] |
| Payment accounts, card data, transaction references and provider payloads | Drop entirely; only the payment_method enum is allowed |
| Database/broker credentials and connection strings | Drop configuration values; emit only an allowlisted error_code |
| Request bodies, event payloads, raw exceptions and query strings | Do not log; select only approved contract fields |
| User-supplied message text and control characters | Use fixed messages; JSON-escape allowed strings and cap correlation_id at 128 characters |

Product and Sale IDs are internal references, not permission to expose logs
publicly. Access and retention require a team policy; no existing deployment
policy is claimed. DEBUG uses the same redaction rules as every other level.

## Proposed severity policy

| Level | When | Sales example |
|---|---|---|
| ERROR | Technical failure prevents a required operation | Product stock lookup times out; required validation cannot complete |
| WARN | Expected business rejection or degraded outcome needing investigation | Requested SaleItem quantity exceeds available stock |
| INFO | Important operation boundary or business outcome | Sale request received or rejected; no payload dump |
| DEBUG | Opt-in diagnostic detail without sensitive values | Active Product validation finished for a bounded item_count |

## Seven synthetic JSON examples

One rejected attempt contains two items. In this illustrative validation fixture,
the first stock check returns insufficient stock and a second check times out.
The fixture collects validation results to demonstrate both required failures;
it does not adopt a production retry, parallelism or fail-fast policy.
No sale is persisted and no SaleRegistered event is published.
For this combined fixture, HTTP 503 prioritizes unavailable required validation;
that precedence is a prototype proposal, not an agreed production API policy.
An isolated insufficient-stock rejection uses the documented HTTP 422.
The 3000 ms timeout is an illustrative test value, not an existing setting.

```jsonl
{"timestamp":"2026-10-21T00:00:00.000Z","level":"INFO","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000001","correlation_id":"w11-sale-attempt-001","event":"sale.request.received","message":"Sale registration requested","duration_ms":0,"route":"/api/v1/sales","method":"POST","item_count":2,"payment_method":"CASH"}
{"timestamp":"2026-10-21T00:00:00.020Z","level":"INFO","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000002","correlation_id":"w11-sale-attempt-001","event":"sale.customer.validated","message":"Customer validation completed","duration_ms":20,"downstream_service":"customer-service"}
{"timestamp":"2026-10-21T00:00:00.040Z","level":"DEBUG","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000003","correlation_id":"w11-sale-attempt-001","event":"sale.products.validated","message":"Active product validation completed","duration_ms":20,"downstream_service":"inventory-service","item_count":2}
{"timestamp":"2026-10-21T00:00:00.060Z","level":"WARN","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000004","correlation_id":"w11-sale-attempt-001","event":"sale.stock.rejected","message":"Requested quantity exceeds available stock","duration_ms":20,"downstream_service":"product-service","product_id":"550e8400-e29b-41d4-a716-446655440000","quantity":2,"stock_available":1,"rule":"INSUFFICIENT_STOCK"}
{"timestamp":"2026-10-21T00:00:03.060Z","level":"ERROR","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000005","correlation_id":"w11-sale-attempt-001","event":"sale.stock.timeout","message":"Required stock lookup timed out","duration_ms":3000,"downstream_service":"product-service","product_id":"550e8400-e29b-41d4-a716-446655440001","quantity":1,"error_code":"STOCK_LOOKUP_TIMEOUT"}
{"timestamp":"2026-10-21T00:00:03.061Z","level":"INFO","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000001","correlation_id":"w11-sale-attempt-001","event":"sale.registration.rejected","message":"Validation failed; sale was not persisted","duration_ms":3061,"item_count":2,"rule":"INSUFFICIENT_STOCK","error_code":"STOCK_LOOKUP_TIMEOUT"}
{"timestamp":"2026-10-21T00:00:03.062Z","level":"INFO","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000001","correlation_id":"w11-sale-attempt-001","event":"sale.request.completed","message":"Request completed with unavailable validation","duration_ms":3062,"route":"/api/v1/sales","method":"POST","http_status":503}
```

Filtering trace_id reconstructs receipt, successful preliminary validations,
business rejection, dependency timeout and the unsuccessful HTTP outcome.
All entries are emitted by Sales; downstream names identify dependencies, not
evidence of logs captured from those services. Parent-child relationships belong
to the CP1 trace map; repeated root span_id entries are events in the same span.

# CP3 - Sales instrumentation prototype

## Scope

This standalone Java/Spring Boot workshop module demonstrates proposed
instrumentation. It does not implement the Sales backend, authentication,
PostgreSQL, RabbitMQ, customer validation or real sale persistence.
Do not deploy it as the business API. It is intended for localhost only.

Project references in the supplied team documentation:

- `09-microservices/services/06-sales-service/README.md`: sales-service, Sale,
  SaleItem and the selected Java/Spring Boot stack.
- `07-api/contracts/openapi/sales-service.yaml`: proposed sale registration.
- `07-api/contracts/openapi/product-service.yaml`: proposed stock lookup.
- CP1 and CP2 define the journey, trace-context proposal and JSON log contract.

## Requirements and commands

Use JDK 21 or newer and Maven. Dependencies are declared in `sales-service/pom.xml`.
The verified machine has JetBrains Runtime 25.0.3 and Maven 3.9.16 outside PATH.
From the root of the course fork, PowerShell:

```powershell
$env:JAVA_HOME = 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr'
$maven = 'C:\Users\USUARIO\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd'
$pom = '11-week\workshop\03-instrumentation\sales-service\pom.xml'
& $maven -o -f $pom test
& $maven -o -f $pom spring-boot:run '-Dspring-boot.run.arguments=--server.address=127.0.0.1 --server.port=18085'
```

`-o` uses the dependencies already cached on this machine. On a fresh machine,
remove `-o` so Maven can download them. With Java/Maven on PATH, use `mvn test`
and `mvn spring-boot:run` from the `sales-service` directory instead.
Stop the foreground application with Ctrl+C.

## First milestone: context and logger

The filter accepts valid version-00 traceparent, preserves trace_id and flags,
and generates a new server span_id. Missing, malformed or zero IDs create a new
context. Unsupported versions are restarted; full W3C version negotiation and
tracestate/baggage propagation are outside this minimal prototype.
The response carries the server context. Each request gets an opaque correlation
UUID. No inbound correlation header or credentials are copied into logs.
Only `/api/v1/sales` is instrumented; framework startup logs are not CP2 events.
This is context propagation, not a tracing SDK or exported trace collector.
DEBUG is not enabled by this prototype's fixed event calls.

Executed on 2026-10-21, this machine; actual Maven console excerpt:

```text
[INFO] Running edu.corhuila.bysellens.TraceContextTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

The tests execute the servlet filter and parse JSON log output, including a check
that a supplied password field is dropped. No functional sale endpoint is claimed
by this first milestone; the second milestone adds the stock-validation probe.

## Second milestone: one outbound call and metrics

`POST /api/v1/sales` is a single-item stock-validation probe, not the complete
contract implementation. It accepts customerId, paymentMethod and one SaleItem
shape, but does not verify Customer or Inventory. Input validation returns 400;
insufficient stock returns 422; dependency timeout/failure returns 503.
Success returns HTTP 200 with prototype_only=true and sale_persisted=false,
never a misleading 201. Multi-item registration and stock mutation are absent.

The call is GET /api/v1/products/{id}/stock. A distinct child span_id and the
incoming trace_id are sent in traceparent. Only configured loopback HTTP is
allowed; no Authorization header is forwarded. The local Product test double
returns only quantity, the response field needed for this probe, not the full
Product service or its security contract. No production credentials are needed.
The default timeout is 3000 ms, a prototype choice. Integration tests use 500 ms.

In terminal A, from the course fork root, run the dependency fixture:

```powershell
& 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr\bin\java.exe' '11-week\workshop\03-instrumentation\sales-service\src\test\java\edu\corhuila\bysellens\ProductStub.java'
```

In terminal B, use the Maven run command above. Both servers bind to loopback;
18082 and 18085 are workshop demo ports, not verified project deployment ports.
Override the Product origin with `--product.base-url=http://127.0.0.1:PORT` or
its timeout with `--product.timeout-ms=3000` in spring-boot.run.arguments.
In terminal C, from the course fork root:

```powershell
$payload = '{"customerId":"750e8400-e29b-41d4-a716-446655440000","paymentMethod":"CASH","items":[{"productId":"550e8400-e29b-41d4-a716-446655440000","quantity":2,"unitPrice":35000}]}'
$headers = @{traceparent='00-91c786ab12de45ff8076aabd34cda109-a100000000000001-01'}
$response = Invoke-WebRequest -UseBasicParsing -Uri 'http://127.0.0.1:18085/api/v1/sales' -Method Post -ContentType 'application/json' -Headers $headers -Body $payload
$response.StatusCode
$response.Headers['traceparent']
$response.Content
(Invoke-WebRequest -UseBasicParsing -Uri 'http://127.0.0.1:18085/metrics').Content
```

## Actual second-milestone evidence

Executed on this machine on 2026-10-21. Live console/HTTP excerpts:

```text
Product test double listening on 127.0.0.1:18082; quantity=15
HTTP 200
traceparent: 00-91c786ab12de45ff8076aabd34cda109-e77add713ff224f3-01
{"stock_validation":"passed","prototype_only":true,"sale_persisted":false}
PRODUCT_STUB traceparent=00-91c786ab12de45ff8076aabd34cda109-468a9de0c6ee40d8-01
sales_requests_total{route="/api/v1/sales",outcome="success"} 1
sales_request_duration_seconds_count{route="/api/v1/sales"} 1
sales_request_duration_seconds_sum{route="/api/v1/sales"} 0.2837621
```

Actual structured application log from that request:

```json
{"timestamp":"2026-10-21T21:07:42.953930500Z","level":"INFO","service":"sales-service","version":"0.0.1","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"468a9de0c6ee40d8","correlation_id":"da994885-c052-43f3-82c7-1b3df0a0f887","event":"sale.stock.validated","message":"Prototype stock validation completed","duration_ms":105.4605,"product_id":"550e8400-e29b-41d4-a716-446655440000","downstream_service":"product-service"}
```

Actual Maven console excerpt after the logging correction:

```text
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.321 s -- in edu.corhuila.bysellens.PropagationTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.025 s -- in edu.corhuila.bysellens.TraceContextTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

PropagationTest starts real HTTP servers on ephemeral loopback ports, asserts
received trace_id equals downstream trace_id, checks distinct span IDs, and
verifies no Bearer forwarding. It covers success, missing context, insufficient
stock, timeout, malformed/non-200 dependency replies, invalid input and metrics.
The stub and Spring context close after tests; manual servers stop with Ctrl+C.

`/metrics` uses Prometheus text format: a request counter labeled by bounded
outcome (success/rejected/error) and route template, plus a latency histogram.
Buckets are 0.05, 0.1, 0.25, 0.5, 1, 3, 5 seconds and +Inf; counts are cumulative.
Scrapes do not increment these metrics. Counters reset on restart; no real
Prometheus scraping, dashboard, alerting or production traffic is claimed.

During execution, the initial integration-test import failed because Spring
Boot 4 places ServletWebServerApplicationContext under web.server.servlet.context.
The import was corrected using the installed artifact. Spring WEB debug logs
also exposed input details; the web log level is now WARN. Application events
use only allowlisted fields and fixed messages. Framework startup logs are
separate from the CP2 JSON contract. The full local Maven output is ignored
under sales-service/target/cp3-console.txt and can be regenerated with `test`.

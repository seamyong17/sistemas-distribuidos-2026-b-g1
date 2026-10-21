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

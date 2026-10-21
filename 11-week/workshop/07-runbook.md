# CP8 - Sales fast-burn runbook

## Scope, ownership and sources

Alert: SalesAvailabilityFastBurn from CP7; proposed Sales availability >=99%/30d.
This runbook covers the workshop prototype and the documented future journey.
There is no verified production deployment, dashboard, notification receiver,
database, RabbitMQ topology, feature flag, circuit breaker or scaling procedure.
Commands below are local diagnostics, not invented production operations.

Team sources in the supplied archive:

- `09-microservices/services/06-sales-service/README.md`: Sale and SaleItem are
  Sales-owned; required validation precedes persistence; 201 is not stock completion.
- `07-api/contracts/openapi/product-service.yaml`: Product owns Stock lookup.
- `09-microservices/services/06-sales-service/events.md`: SaleRegistered and
  StockUpdated; publication recovery and multi-item completion remain unresolved.
- CP3 HOW-TO-RUN supplies the executed local commands and Product test double.
- CP6/CP7 supply SLO population, thresholds and low-traffic limitations.

## Symptoms

Sales requests return technical failures, including 503 when required Product
stock validation times out. The fast alert means both the 1h and 5m error ratios
exceed 14.4%, not that CPU is high or every sale has failed. Inspect both windows.
An alert can coexist with a latency SLO that passes because fast errors are still
short responses. An absent alert during no traffic is not proof of recovery.

## Confirm the signal

In a future Prometheus instance with CP7 rules and isolated environment data:

```promql
sales:availability_error_ratio:rate1h / 0.01
sales:availability_error_ratio:rate5m / 0.01
sum(increase(sales_requests_total{route="/api/v1/sales",outcome="error"}[5m]))
sum(increase(sales_requests_total{route="/api/v1/sales",outcome=~"success|error"}[5m]))
```

Confirm both burns exceed 14.4 and inspect the actual event counts. A single
failure is significant at this shop's estimated volume; do not invent a trend
from one event or interpret NaN/no series as zero failures.
Locally, inspect http://127.0.0.1:18085/metrics using the CP3 commands.
Counters alone cannot reproduce the rolling alert without stored scrape history.

Use this JSON-log query against an actual captured console file, replacing the
example trace ID with the affected request's trace_id:

```powershell
$traceId = '91c786ab12de45ff8076aabd34cda109'
Get-Content '.\sales-console.log' | ForEach-Object {
    if ($_ -match '^\{') {
        try { $entry = $_ | ConvertFrom-Json -ErrorAction Stop } catch { return }
        if ($entry.trace_id -eq $traceId) { $entry }
    }
}
```

To capture a local run, pipe the CP3 Maven run command to
`Tee-Object -FilePath .\sales-console.log`; this filename is an operator-created
capture, not a pre-existing repository artifact or deployed log platform.
Look for sale.stock.timeout or sale.stock.failed, then the completed HTTP status.
Compare Product child span_id and Sales parent context from the CP1 map.
Never paste tokens, customer details or complete request bodies into incident notes.

## First mitigations, ordered by risk

1. **Pause new confirmation attempts.** Stop the manual demo requests or ask the
   administrator to pause new submissions while preserving the current cart.
   This requires no feature flag or code change. It prevents repeated attempts
   and limits new exposure; it does not restore availability or undo old effects.
2. **Restore the affected dependency in its known test configuration.** In the
   isolated exercise, stop the deliberately delayed Product fixture and start
   the unmodified CP3 ProductStub command, which returns quantity=15. Only stop
   processes identified as belonging to this exercise. For a future real Product
   incident, its owner must approve and execute an evidence-based recovery;
   the stub must never replace production Product or supply invented real stock.
3. **Roll back a verified faulty prototype change if mitigation 2 is insufficient.**
   Review the diff against the known-working CP4 state de973ec, reproduce the
   failure, and make a reviewed revert commit of the specific offending change.
   Preserve published history; no force push, reset of remote history or blind
   bulk revert. Re-run the CP3 tests and restart only the local demo process.
   There is no verified production image/deployment rollback procedure yet.

Do not increase the stock timeout simply to hide errors, replay an event, alter
stock manually or retry an uncertain real sale without checking its outcome.
CP3 has no persistence and returns sale_persisted=false; that is a prototype fact,
not a guarantee for the future business API. Scale-out and circuit-breaking
remain design options, not supported steps in this current runbook.

## Escalation by role

| Role | Action |
|---|---|
| Sales owner: Seam Yong Agudelo | Gather trace/log/metric evidence, pause exercise requests, coordinate and verify Sales |
| Product owner: team member to be confirmed | Investigate stock lookup and Product consumer; approve real dependency recovery |
| Inventory/Customer owners: team members to be confirmed | Investigate their validation only when traces identify those hops |
| Team coordinator: assignment to be confirmed | Coordinate cross-component/release decisions and unresolved consistency risks |

No phone number, on-call rotation, named teammate or automatic escalation path
has been verified. Use existing team contact arrangements; do not claim one here.

## Verify recovery and close

Run the CP3 tests: the established baseline is 10 passing tests. Send one local
probe using a new trace context; verify HTTP 200, stock_validation=passed,
sale_persisted=false and downstream propagation. Check that the Product response
is from the intended fixture and that no sensitive body data appears in logs.
With real scraping, verify new valid requests succeed and both burn windows
fall below threshold. The long window can retain past failures after recovery;
the short-window condition must also be checked before declaring the alert clear.
No-traffic silence is insufficient evidence. Resume confirmations only after
observed successful recovery; record timeline, impact, mitigation and unresolved
work in CP9. Production recovery additionally requires verified Sale/Stock state,
which the current prototype cannot establish.

Validation: the four diagnostic PromQL expressions passed syntax parsing.
The PowerShell trace filter was exercised against actual CP4 console output;
this does not demonstrate a live production alert or a completed recovery drill.

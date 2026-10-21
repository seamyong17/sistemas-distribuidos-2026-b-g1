# CP6 - SLIs, SLOs and error budget

## Scope and evidence

These are proposed workshop objectives, not achieved service levels or an SLA.
The student described a new business, a hypothetical maximum of about 15 sales
per day, and office hours 09:00-18:00 America/Bogota. No backend traffic is measured.
Use 15 as an upper planning case, not a claim about minimum/average actual sales.
The student selected a 10-second confirmation response expectation in CP4 and
requested a recommendation for percentages. The starting proposal is 99%
availability, 95% latency compliance and 99% event-attempt reliability over 30 days.
These initial targets must be reviewed with the team and real users after rollout.

Team sources in the supplied documentation archive:

- `03-product/vision.md`: reliable sales registration and stock updates.
- `09-microservices/services/06-sales-service/README.md`: Sales owns Sale and
  SaleItem, validates references/Stock and does not prove stock completion by 201.
- `07-api/contracts/openapi/sales-service.yaml`: proposed POST /api/v1/sales.
- `09-microservices/services/06-sales-service/events.md`: Product consumes
  SaleRegistered to update Stock; Sales consumes StockUpdated; recovery is pending.
- CP4 supplies every metric family below; CP3/CP4 implement only the Sales
  request counter and histogram. Consumer metrics are still proposed.

## Measurement boundaries and targets

All windows are rolling 30 calendar days. Observe requests whenever they occur;
the business-hours assumption is used only for the traffic/time illustration.
Availability considers completed Sales requests classified success or error;
rejected (4xx) requests are excluded as invalid/business-rejected attempts.
Latency considers all completed Sales requests because the histogram has no
outcome label; a fast 503 can meet latency while failing availability.
Event reliability considers completed non-duplicate SaleRegistered handler
attempts in Product. It is not a unique-sale completion or exactly-once SLO.

| SLO | Good / valid events | Target | Window | Observation status |
|---|---|---|---|---|
| Sales availability | Successful completed Sales requests / (successful + technically failed completed Sales requests) | >=99% | Rolling 30d | Prototype metric exists; real Sales backend absent |
| Sales latency | Completed Sales responses taking <=10 seconds / all completed Sales responses | >=95% | Rolling 30d | Exact 10-second bucket exists; prototype only |
| Stock event-attempt reliability | Successful Product SaleRegistered handler attempts / (success + error handler attempts) | >=99% | Rolling 30d | Consumer metric proposed; not implemented |

The first two become real journey SLIs only after instrumentation surrounds
actual sale registration. CP3's successful stock probe persists no Sale.
The third complements HTTP availability because HTTP success does not establish
successful asynchronous stock processing. Retries count as additional attempts;
duplicates are excluded from both numerator and denominator.

## Exact PromQL using CP4 metrics

Availability SLI (ratio, not percent):

```promql
sum(increase(sales_requests_total{route="/api/v1/sales",outcome="success"}[30d]))
/
sum(increase(sales_requests_total{route="/api/v1/sales",outcome=~"success|error"}[30d]))
```

Latency SLI; le="10.0" matches the prototype's actual exported bucket label:

```promql
sum(increase(sales_request_duration_seconds_bucket{route="/api/v1/sales",le="10.0"}[30d]))
/
sum(increase(sales_request_duration_seconds_count{route="/api/v1/sales"}[30d]))
```

Event-attempt reliability SLI (requires proposed consumer instrumentation):

```promql
sum(increase(bysellens_events_processed_total{service="product-service",event="SaleRegistered",outcome="success"}[30d]))
/
sum(increase(bysellens_events_processed_total{service="product-service",event="SaleRegistered",outcome=~"success|error"}[30d]))
```

Use increase before sum to account for individual counter resets. Multiply by
100 only for percentage display; compare these ratios with 0.99, 0.95 and 0.99.
Zero valid events means undefined compliance, not 100% success. Missing metrics
mean unknown coverage. Do not replace either case with a healthy constant.
Prometheus scraping and sufficient retention/history must exist before evaluating
a full 30-day window. No live PromQL execution or 30-day compliance is claimed.
Server counters miss requests that never reach/complete at Sales and consumer
counters miss unpublished, lost or indefinitely queued messages. Future external
checks and publication/queue monitoring are needed; these SLOs do not cover them.

## Hypothetical traffic and budget arithmetic

Let D be actual operating days within the rolling window; D is not confirmed.
Assume one valid confirmation attempt per intended sale, no retries, negligible
rejections, and a uniform arrival rate during the stated 9-hour opening period.
These are calculation assumptions, not measured customer behavior.
Then N=15*D valid requests and T=9*60*D=540*D operating minutes.
For the upper illustrative case D=30: N=450 and T=16,200 minutes (270 hours).
The opening-time rate is 15/540=1/36 requests per minute (about 1.67 per hour).
One nominal SaleRegistered per accepted sale gives up to 450 baseline events;
real handler attempts will differ with rejections, failures and retries.

For target S, budget fraction e=1-S. Allowed bad events=e*valid_events.
At the illustrative 450-event/request population:

| SLO | Budget fraction | Allowed bad events | Whole bad-event maximum | Operating-time equivalent | Calendar-time equivalent |
|---|---|---|---|---|---|
| Availability 99% | 1% | 450*0.01=4.5 failed requests | 4 | 16,200*0.01=162 minutes | 43,200*0.01=432 minutes |
| Latency 95% | 5% | 450*0.05=22.5 responses over 10s | 22 | 16,200*0.05=810 minutes | 43,200*0.05=2,160 minutes |
| Event attempts 99% | 1% | 450*0.01=4.5 failed attempts | 4 | 16,200*0.01=162 minutes | 43,200*0.01=432 minutes |

Whole-event maxima round down; budgets are recalculated with each SLO's actual
denominator, not fixed forever at 450. For availability, 4 failures/450 gives
99.111...% success; 5/450 gives 98.888...% and exceeds the proposed objective.
The primary request-budget equivalent is 162 operating minutes, or 2h42m, only
under uniform opening-time traffic and total failure during those minutes.
The 432-minute figure instead assumes uniform 24/7 traffic. They are alternative
normalizations, not additive allowances. Neither is a measured uptime allowance.
Latency/event equivalents are arithmetic normalizations, not actual downtime.
For another operating-day count, use 0.15*D availability failures and 5.4*D
operating minutes. At zero traffic, request budget/compliance cannot establish
availability; no traffic must not imply that the service was healthy all day.

## Proposed team error-budget policy

Consumption = bad_events / ((1-S)*valid_events). Assess each SLO separately;
do not transfer unused latency budget to availability or add their allowances.

| Consumed | Proposed action | Availability illustration at N=450 |
|---|---|---|
| <50% | Continue reviewed work; monitor failure trends | Fewer than 2.25 failures |
| >=50% | Prioritize Sales reliability; review the failing trace and Product dependency before risky changes | 2.25 failures; crossed at 3 whole failures |
| >=75% | Defer nonessential Sales releases; investigate/mitigate and require recovery evidence | 3.375 failures; crossed at 4 whole failures |
| >=100% | Freeze nonessential Sales releases; allow only reviewed recovery/security fixes until compliance recovers | 4.5 failures; crossed at 5 whole failures |

The Sales owner proposes the release decision; the responsible Product owner
investigates stock-handler failures. Team-wide approval is not claimed.
Resume normal work only after measured recovery and review of the rolling
window. Budget recovers as old bad events age out or additional valid successes
change the ratio, not by manually resetting counters. With this small population,
one failure consumes about 22.22% of the illustrative availability budget.
Burn-rate rules in CP7 must acknowledge sparse traffic; estimated rates and
fractional budgets are not observed fractional sales or guaranteed alert timing.

Technical references: [Google SRE SLOs](https://sre.google/workbook/implementing-slos/)
and [Prometheus histograms](https://prometheus.io/docs/practices/histograms/).

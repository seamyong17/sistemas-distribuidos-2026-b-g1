# CP7 - Availability burn-rate alert policy

## Scope and sources

These proposed rules use CP6's Sales availability target: 99% over rolling 30d.
They do not assert that Prometheus, Alertmanager or notification routing exists.
sales-service/Sale/SaleItem and Product-owned Stock remain the CP1 design.
The prototype validates Stock through a local dependency and persists no Sale.
The student remains responsible for Sales, not for implementing all dependencies.

Team-document evidence in the supplied archive:

- `09-microservices/services/06-sales-service/README.md`: required validation
  before creating Sale and SaleItem; HTTP success is not asynchronous completion.
- `07-api/contracts/openapi/sales-service.yaml`: unavailable required validation
  prevents registration; CP3 represents such a failure with HTTP 503.
- `07-api/contracts/openapi/product-service.yaml`: Product owns Stock lookup.
- `09-microservices/services/06-sales-service/events.md`: SaleRegistered and
  StockUpdated are separate asynchronous operations, not HTTP availability events.

## Expressions and response policy

error_ratio = rate(error) / rate(success + error), matching CP6 exclusions.
burn_rate = error_ratio / (1-0.99) = error_ratio / 0.01.
The recording rules apply rate before sum to handle individual counter resets.
Every ratio combines only Sales route /api/v1/sales, not dependency requests.

| Alert | Long and short windows | Threshold in both windows | Proposed handling |
|---|---|---|---|
| SalesAvailabilityFastBurn | 1h AND 5m | burn >14.4, equivalently error ratio >14.4% | Critical; Sales owner investigates immediately using CP8 |
| SalesAvailabilitySlowBurn | 6h AND 30m | burn >6, equivalently error ratio >6% | Warning; Sales owner investigates sustained degradation |

The short window confirms that the problem is recent; it is not a `for: 5m`
or `for: 30m` waiting period. No extra for duration is imposed. A rule fires at
the first 30-second evaluation where both window comparisons are true.
This does not mean a new incident waits a full hour or six hours to fire;
history, event distribution and scrape/evaluation timing determine detection.
Severity/team are proposed labels, not evidence of delivered notifications.
Both may fire; no Alertmanager inhibition or receiver configuration is claimed.
The runbook_url is the required destination planned for CP8, not an existing
runbook at CP7 creation. It must be checked once CP8 is published.

## Budget arithmetic with the Sales planning numbers

CP6 assumes at most 450 valid requests in the illustrative 30-operating-day case,
so the availability budget is 450*0.01=4.5 failed requests. The 30-day window is
720 calendar hours; the stated opening schedule totals only 270 operating hours.

Under the conventional uniform-24/7 normalization:

| Alert at threshold | Formula | Budget fraction | Failures at budget 4.5 | Equivalent outage minutes (432-minute calendar budget) |
|---|---|---|---|---|
| Fast, sustained for 1h | 14.4*1/720 | 2% | 0.09 | 8.64 |
| Slow, sustained for 6h | 6*6/720 | 5% | 0.225 | 21.6 |

This is the workshop's reference arithmetic, not a guarantee of consumption
before the first notification. The rules use strict greater-than thresholds.
With uniform traffic only during 09:00-18:00, a window entirely inside opening
hours instead represents these hypothetical amounts at threshold:

| Alert | Expected requests in long window | Failed requests at threshold | Fraction of the 4.5-request budget |
|---|---|---|---|
| Fast | 15/9*1=1.6667 | 1.6667*0.144=0.24 | 5.3333% |
| Slow | 15/9*6=10 | 10*0.06=0.6 | 13.3333% |

These also equal 8.64 and 21.6 equivalent operating outage minutes under the
uniform-rate model, using CP6's 162-minute operating budget. They are not actual
fractional customers or counted failures. Windows spanning closed hours have
different request populations. At this scale, one observed failure already
consumes about 22.22% of the illustrative monthly request budget.

## Sparse traffic and missing data

The expected 15/day is an upper planning scenario, not a load measurement.
There may be no requests in a 5-minute interval; a single event can dominate
the error ratio. Do not promise minute-exact real alerting from this model.
Zero denominator yields NaN; comparisons do not fire. Missing series similarly
cannot demonstrate healthy service. No `or vector(0)` masks absent telemetry.
No minimum-volume gate is added because it could hide this small shop's failures
and would change the required exercise. Review telemetry separately and retain
manual failure reports. External probes would need a separately approved design.
Use an isolated environment's series; cross-environment aggregation is unsafe.
Counter snapshots alone are insufficient: the rules require continuous scraping.

## Two cause-based alerts intentionally excluded

1. CPU usage alone above a fixed percentage: no measured capacity baseline exists,
   and CPU can rise without harming a Sale or Stock lookup. Keep it diagnostic,
   rather than paging without evidence of user-facing budget consumption.
2. RabbitMQ queue depth alone above an arbitrary count: queues/bindings and normal
   backlog are not provisioned or measured. A backlog can reflect ordinary bursts;
   it does not by itself establish failure of Sales HTTP registration. Consumer
   health needs the distinct proposed CP4 metrics and real topology first.

Technical references: [SRE multi-window alerts](https://sre.google/workbook/alerting-on-slos/)
and [Prometheus alert rules](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/).

Validation: SnakeYAML 2.6 parsed the file; four recording rules, two alerts and
required labels/annotations were checked locally. No promtool or Prometheus
expression evaluation was run; live notification behavior remains unverified.

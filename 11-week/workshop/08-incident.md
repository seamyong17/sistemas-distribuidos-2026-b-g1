# CP9 - Simulated incident and blameless postmortem

## Exercise status, scope and evidence

This is a mathematical tabletop simulation, not a real outage or a recorded
40-minute load test. The timeline, spans and four JSON logs below are synthetic.
CP3/CP4 actually ran 10 passing tests; those tests do not prove this drill occurred.
CP1 selects Sales -> Product, GET /api/v1/products/{id}/stock. CP2 supplies the
log contract; CP4 supplies metric families; CP6 supplies SLOs and traffic;
CP7 supplies alert expressions; CP8 supplies mitigation and recovery steps.

Team sources in the supplied archive:

- `09-microservices/services/06-sales-service/README.md`: Sale/SaleItem validation
  precedes persistence and a failed required stock validation prevents registration.
- `07-api/contracts/openapi/product-service.yaml`: Product-owned Stock lookup.
- `09-microservices/services/06-sales-service/events.md`: SaleRegistered and
  StockUpdated; HTTP creation is distinct from asynchronous stock completion.

## Injected scenario and explicit assumptions

At simulated 15:00, 25% of Product stock calls take 3 seconds for 40 minutes;
the remaining 75% are fast. Each request has one item and one stock lookup.
There are no rejections, retries or other failures. Assume six hours of healthy
history from 09:00, a constant expected rate of 15/540=1/36 requests/minute,
and subsequent healthy traffic until the end of business hours.
The slow quarter is distributed continuously in the analytical model, not as
observed fractional customers. Evaluate alerts every 30 seconds from minute 0.

CP3's default timeout is also 3 seconds. At that exact boundary, real network
scheduling can make a response win or lose the deadline. For this simulation
the deadline wins for all affected calls, producing HTTP 503/stock timeout.
This is an added modeling assumption, not a guaranteed behavior of the code.
If all 3-second calls instead complete before timeout, latency <=10 seconds
still passes and neither availability alert fires. Delays alone are not errors.

## One slow request at simulated 15:35

All spans share trace_id 91c786ab12de45ff8076aabd34cda109; IDs below are synthetic.

- Sales UI root: 3016 ms, span a100000000000001.
  - sales-service POST /api/v1/sales: 3012 ms, span a100000000000002.
    - Customer validation: 4 ms, span a100000000000003.
    - Inventory active-product validation: 6 ms, span a100000000000004.
    - Product stock HTTP call: 3000 ms, span a100000000000005; timeout wins.
      - Product handler: 3000 ms injected delay, simulated server span a100000000000006.
    - Failure response handling: 2 ms within the Sales span.

UI/network overhead is 4 ms. No Sales persistence span, Sale ID, SaleRegistered
publication or stock mutation occurs on this failed path. The Product child
receives traceparent with the same trace_id and the sending client span_id.

```jsonl
{"timestamp":"2026-10-21T20:35:00.000Z","level":"INFO","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000002","correlation_id":"w11-drill-001","event":"sale.request.received","message":"Sale registration requested","duration_ms":0,"route":"/api/v1/sales","method":"POST","item_count":1}
{"timestamp":"2026-10-21T20:35:00.010Z","level":"DEBUG","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000005","correlation_id":"w11-drill-001","event":"sale.stock.requested","message":"Required stock validation started","duration_ms":0,"downstream_service":"product-service","product_id":"550e8400-e29b-41d4-a716-446655440000","quantity":2}
{"timestamp":"2026-10-21T20:35:03.010Z","level":"ERROR","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000005","correlation_id":"w11-drill-001","event":"sale.stock.timeout","message":"Required stock validation timed out","duration_ms":3000,"downstream_service":"product-service","error_code":"STOCK_LOOKUP_TIMEOUT"}
{"timestamp":"2026-10-21T20:35:03.012Z","level":"ERROR","service":"sales-service","version":"workshop-prototype","environment":"workshop","trace_id":"91c786ab12de45ff8076aabd34cda109","span_id":"a100000000000002","correlation_id":"w11-drill-001","event":"sale.request.completed","message":"Sale not persisted because required validation failed","duration_ms":3012,"route":"/api/v1/sales","method":"POST","http_status":503}
```

DEBUG here is an illustrative CP2 event; CP3 does not emit sale.stock.requested.

## SLI values and alert calculation

During the fault, expected HTTP availability is 75%. Every response completes
within 10 seconds, so latency compliance remains 100%, including the fast errors.
The successful 75% may publish SaleRegistered in the future design; assume all
such handler attempts succeed, giving modeled event-attempt reliability 100%.
The failed quarter publishes no event and is absent from that third denominator;
event reliability cannot substitute for end-to-end Sales availability.

For elapsed incident time t, the long-window error ratio before minute 40 is
0.25*t/60 for 1h and 0.25*t/360 for 6h. Five-minute confirmation is 0.25 once
t>=5. Fast burn requires t>60*0.144/0.25=34.56 minutes. The first 30-second
evaluation meeting both conditions is minute 35 (15:35). There is no extra for.
The maximum 6h error ratio is 0.25*40/360=2.7778%, burn 2.7778, below slow burn 6;
the slow alert never fires in this model, even though its short window can pass.

| Simulated minute | 1h burn | 5m burn | Fast alert / action |
|---|---|---|---|
| 0 / 15:00 | 0 | 0 | Fault injection begins |
| 5 / 15:05 | 2.0833 | 25 | Long-window threshold not met |
| 34.5 / 15:34:30 | 14.375 | 25 | Still below fast long-window threshold |
| 35 / 15:35 | 14.5833 | 25 | Fast alert first fires; Sales owner investigates |
| 40 / 15:40 | 16.6667 | 25 | CP8 step 2 restores the normal test dependency; injection ends |
| 42 / 15:42 | 16.6667 | 15 | Fast alert remains active |
| 42.5 / 15:42:30 | 16.6667 | 12.5 | Fast alert clears because short-window condition is false |

This timeline retains incoming traffic for the mathematical comparison. CP8 step
1 can pause new attempts, but pausing alone cannot demonstrate service recovery.
Step 2 removes the injected cause and stops new failures in the simulation.
With real discrete traffic near 0-15 sales/day, there is no guaranteed minute-35
notification: a single failure can dominate the windows or no event can occur.
Prometheus scraping/notification delivery was not executed for this tabletop.

## Impact and blameless root-cause review

Expected attempted registrations in 40 minutes: 40/36=1.1111; expected failed
registrations: 0.25*40/36=0.2778. These fractional expectations are not observed
sales, lost revenue or a report of actual affected people. The unaffected 75%
yield 0.8333 expected successful registrations during the same interval.
Assuming CP6's fixed 450-request monthly planning population, modeled
availability is 99.93827%; consumed availability budget is 6.17284%, calculated
using the unrounded expected failure count (0.25*40/36), not its displayed rounding.
Equivalent full-failure operating time is 40*0.25=10 minutes, also 10/162=6.17284%
of the operating-time equivalent. Do not count the whole 40 minutes as full outage.
No latent backlog, duplicate deduction, real sale persistence or data loss is proven.

The simulated root cause is deliberately injected Product stock-handler delay
meeting the configured deadline. No database lock, CPU issue or individual error
is asserted. Contributing conditions are the shared 3-second delay/deadline,
Sales' required stock dependency and sparse traffic, which makes ratio alerts
volatile. The 10-second response objective does not protect HTTP availability.
The trace/log contract provides diagnosis; it does not itself prevent the fault.

## Proposed follow-up user stories

These are proposed backlog entries, not adopted HU IDs or completed tasks.

1. **As the Sales owner, I want repeatable dependency-delay tests so that deadline
   failures are distinguishable from business rejection.** Acceptance: a scripted
   four-request fixture delays exactly one call beyond the configured deadline;
   assert one 503, three successes, unchanged trace IDs, distinct child spans,
   one technical-error counter increment and no sensitive body/token logs.
2. **As the Product owner, I want measured stock-handler latency so that Sales
   deadline failures can be diagnosed.** Acceptance: instrument the proposed CP4
   Product histogram; measure below/above-deadline cases without inventing a
   production cause, and demonstrate normal fixture recovery without stock writes.
3. **As the team coordinator, I want an agreed Sale/Stock failure policy so that
   late asynchronous failures are not mistaken for completed sales.** Acceptance:
   align Sales, Product and architecture documents; specify 201 versus stock
   completion and recovery ownership; review a failure sequence and test plan.
   Select no new event, retry policy or recovery mechanism without team agreement.

Validation: JSON syntax/fields and the 30-second analytical timeline are checked
locally. This is model validation, not a production postmortem or live alert test.

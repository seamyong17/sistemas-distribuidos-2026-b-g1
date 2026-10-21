# CP4 - Golden signals and metric catalog

## Scope, evidence and ownership

The journey is the CP1 proposed registration of a Sale and asynchronous Stock
processing. Only the CP3 Sales prototype currently exports request metrics.
Its Product HTTP stub exports no metrics. No production traffic, Prometheus
scraping, dashboard, service deployment or saturation measurement is claimed.
Existing means implemented and exercised locally in CP3, not deployed.
Proposed means a specification requiring future instrumentation and validation.
Dependencies are included to explain Sales; their implementation is not the
student's individual responsibility in this workshop.

Team sources in the supplied bysellens-docs-main archive:

- `03-product/vision.md`: reliable sales registration and stock updates.
- `09-microservices/services/06-sales-service/README.md`: Sale and SaleItem;
  customer, active-product and stock validation before registration.
- `07-api/contracts/openapi/customer-service.yaml`: customer detail route.
- `07-api/contracts/openapi/inventory-service.yaml`: active-product detail route.
- `07-api/contracts/openapi/product-service.yaml`: Product owns Stock lookup.
- `09-microservices/services/06-sales-service/events.md`: SaleRegistered is
  consumed by Product; StockUpdated is consumed by Sales; completion is pending.
- `03-instrumentation/sales-service/src/main/java/edu/corhuila/bysellens/SalesMetrics.java`:
  verified Sales metric names, labels and classic histogram buckets.

## Shared interpretation

Latency measures elapsed time in seconds; traffic counts completed operations;
errors count technical failures; saturation uses simultaneous in-flight work.
In-flight gauges are pressure indicators, not utilization percentages: no tested
worker/pool capacity exists to supply a denominator or saturation threshold.
HTTP counters classify <400 as success, 400-499 as rejected and >=500 as error,
matching CP3. Business rejection does not automatically mean service failure.
The HTTP histogram currently includes all outcomes; it cannot isolate successful
latency. Counters and histogram samples reset when the process restarts.
Each HTTP table below covers one CP1 route. Service prefixes isolate families.
Apply rate before sum to preserve per-instance counter reset detection.
Queries assume future Prometheus scrapes; they are not executed query results.

## sales-service

| Signal | Metric | Type | Labels | Dashboard PromQL | Status |
|---|---|---|---|---|---|
| Latency | sales_request_duration_seconds | histogram | route, generated le on buckets | `histogram_quantile(0.95, sum by (le) (rate(sales_request_duration_seconds_bucket{route="/api/v1/sales"}[5m])))` | Existing |
| Traffic | sales_requests_total | counter | route, outcome | `sum(rate(sales_requests_total{route="/api/v1/sales"}[5m]))` | Existing |
| Errors | sales_requests_total | counter | route, outcome | `sum(rate(sales_requests_total{route="/api/v1/sales",outcome="error"}[5m]))` | Existing, same counter |
| Saturation | sales_requests_in_flight | gauge | route | `sum(sales_requests_in_flight{route="/api/v1/sales"})` | Proposed |

In CP3 the route is a stock-validation probe, not persisted sale registration.
Its success counter cannot establish successful real Sale creation or stock
completion. The future backend must place observations at the real boundary.

## customer-service

| Signal | Metric | Type | Labels | Dashboard PromQL | Status |
|---|---|---|---|---|---|
| Latency | customer_request_duration_seconds | histogram | route, generated le on buckets | `histogram_quantile(0.95, sum by (le) (rate(customer_request_duration_seconds_bucket{route="/api/v1/customers/{id}"}[5m])))` | Proposed |
| Traffic | customer_requests_total | counter | route, outcome | `sum(rate(customer_requests_total{route="/api/v1/customers/{id}"}[5m]))` | Proposed |
| Errors | customer_requests_total | counter | route, outcome | `sum(rate(customer_requests_total{route="/api/v1/customers/{id}",outcome="error"}[5m]))` | Proposed, same counter |
| Saturation | customer_requests_in_flight | gauge | route | `sum(customer_requests_in_flight{route="/api/v1/customers/{id}"})` | Proposed |

## inventory-service

| Signal | Metric | Type | Labels | Dashboard PromQL | Status |
|---|---|---|---|---|---|
| Latency | inventory_request_duration_seconds | histogram | route, generated le on buckets | `histogram_quantile(0.95, sum by (le) (rate(inventory_request_duration_seconds_bucket{route="/api/v1/inventory/products/{id}"}[5m])))` | Proposed |
| Traffic | inventory_requests_total | counter | route, outcome | `sum(rate(inventory_requests_total{route="/api/v1/inventory/products/{id}"}[5m]))` | Proposed |
| Errors | inventory_requests_total | counter | route, outcome | `sum(rate(inventory_requests_total{route="/api/v1/inventory/products/{id}",outcome="error"}[5m]))` | Proposed, same counter |
| Saturation | inventory_requests_in_flight | gauge | route | `sum(inventory_requests_in_flight{route="/api/v1/inventory/products/{id}"})` | Proposed |

## product-service

| Signal | Metric | Type | Labels | Dashboard PromQL | Status |
|---|---|---|---|---|---|
| Latency | product_request_duration_seconds | histogram | route, generated le on buckets | `histogram_quantile(0.95, sum by (le) (rate(product_request_duration_seconds_bucket{route="/api/v1/products/{id}/stock"}[5m])))` | Proposed |
| Traffic | product_requests_total | counter | route, outcome | `sum(rate(product_requests_total{route="/api/v1/products/{id}/stock"}[5m]))` | Proposed |
| Errors | product_requests_total | counter | route, outcome | `sum(rate(product_requests_total{route="/api/v1/products/{id}/stock",outcome="error"}[5m]))` | Proposed, same counter |
| Saturation | product_requests_in_flight | gauge | route | `sum(product_requests_in_flight{route="/api/v1/products/{id}/stock"})` | Proposed |

## Asynchronous part of Sales and Product (all proposed)

These custom application metrics observe consumer work, not RabbitMQ internals.
Valid pairs are product-service/SaleRegistered and sales-service/StockUpdated.
No new event, queue, exchange, retry policy or completion guarantee is introduced.

| Signal | Metric | Type | Labels | Dashboard PromQL |
|---|---|---|---|---|
| Latency | bysellens_event_processing_duration_seconds | histogram | service, event, generated le | `histogram_quantile(0.95, sum by (service, event, le) (rate(bysellens_event_processing_duration_seconds_bucket[5m])))` |
| Traffic | bysellens_events_processed_total | counter | service, event, outcome | `sum by (service, event) (rate(bysellens_events_processed_total[5m]))` |
| Errors | bysellens_events_processed_total | counter | service, event, outcome | `sum by (service, event) (rate(bysellens_events_processed_total{outcome="error"}[5m]))` |
| Saturation | bysellens_events_in_flight | gauge | service, event | `sum by (service, event) (bysellens_events_in_flight)` |

Consumer outcomes are success/error/duplicate (three bounded values). Count each
delivery once at handler completion; duplicates do not imply another stock change.
Duration excludes broker waiting time. Processing StockUpdated does not prove
all items of a Sale completed. Queue backlog and end-to-end event delay remain
coverage gaps until broker provisioning and correlation semantics are resolved.

## Cardinality budget and forbidden labels

| Label | Estimated distinct values | Scope / restriction |
|---|---|---|
| route | 1 per HTTP service | Exact template shown above; no IDs or query strings |
| outcome | 3 | HTTP: success/rejected/error; consumer: success/error/duplicate |
| le | 9 | Eight finite classic buckets plus +Inf; exporter-generated |
| service | 2 for event metrics | Only Sales and Product consumer owners |
| event | 2, with only 2 valid service/event pairs | SaleRegistered or StockUpdated |
| job / instance | Deployment-dependent, not measured | Scrape target labels; multiply estimates by actual target counts |

Per HTTP target: counter 1*3=3 series; histogram 1*(9+2)=11 series (including
sum/count); proposed gauge 1=1 series. Total 15 per service; Sales currently has
14 because the saturation gauge is absent. Four future HTTP targets total 60.
Event families: 2 valid pairs*3 outcomes=6 counter series, 2*11=22 histogram
series and 2 gauges, totaling 30. Combined planned scope: 90 per single set of
targets, excluding infrastructure metrics and extra scrape labels. This is a
design estimate, not measured storage usage or an adopted infrastructure limit.
Never label by user_id, customer_id, sale_id, product_id, email, trace_id,
span_id, eventId, correlation_id, raw URL, exception text or arbitrary status.
Replace raw URLs with route templates and dynamic error text with bounded
outcome values. Keep trace correlation in protected logs/traces instead.

## Latency histogram buckets and user expectations

The student selected up to 10 seconds as the proposed acceptable confirmation
response time, excluding asynchronous stock completion. This is a design
expectation, not a measured baseline or a finalized CP6 SLO percentage/window.
CP3 initially exposed 0.05, 0.1, 0.25, 0.5, 1, 3 and 5 seconds plus +Inf;
CP4 adds an explicit 10-second bucket to SalesMetrics for exact threshold counts.
Lower buckets distinguish quick checks; 3 seconds matches the prototype's
dependency timeout and CP9's slow call; 5 and 10 show remaining response headroom;
+Inf retains slower operations. The 10-second expectation does not change the
3-second dependency timeout or guarantee that the future backend meets it.
Proposed dependency/consumer histograms reuse these bounds; tune with real data.

## Query interpretation and validation limits

Traffic/error queries return operations per second; p95 queries return seconds,
not percentages. Gauges count active operations, not requests per second.
For an HTTP error fraction, divide the error rate by the rate of the same
service's requests. Do not count a rejected sale twice or add dependency traffic
to the Sales denominator. Zero traffic gives an undefined ratio; missing metrics
and empty histograms must display no data, not healthy zero latency.
CP3's HTTP tests verified metric exposition and scrape exclusion. No live
Prometheus engine has evaluated these catalog queries or measured saturation.

Technical references: [Prometheus naming](https://prometheus.io/docs/practices/naming/),
[PromQL functions](https://prometheus.io/docs/prometheus/latest/querying/functions/),
[Google SRE golden signals](https://sre.google/sre-book/monitoring-distributed-systems/).

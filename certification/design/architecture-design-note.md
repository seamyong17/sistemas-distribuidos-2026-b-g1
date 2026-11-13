# Architecture design note — By_Sellens

## 1. Target architecture

![Target architecture](target-architecture.png)

| Component in the diagram | Defined in (file of your team's -docs) |
|---|---|
| React administration frontend | `05-architecture/overview.md` |
| API Gateway | `09-microservices/services/01-api-gateway/README.md` |
| Authentication service | `09-microservices/services/02-auth-service/README.md` |
| Product service and current stock | `09-microservices/services/03-product-service/README.md` |
| Inventory service | `09-microservices/services/04-inventory-service/README.md` |
| Customer service | `09-microservices/services/05-customer-service/README.md` |
| Sales service | `09-microservices/services/06-sales-service/README.md` |
| PostgreSQL data and ownership | `06-data/models.md` |
| RabbitMQ and event flows | `10-devops/environments.md`, `13-operations/CONSISTENCY-RECONCILIATION.md` |

By_Sellens provides an administrative interface for customers, products, inventory, and sales. Its code archive includes a React shell and individual frontend modules, a Spring backend organized by service, and a local deployment defined in Docker Compose. The proposed target runs the frontend and backend containers on Huawei Cloud CCE. An internet-facing load balancer exposes the web and API entry points over HTTPS. The frontend sends API requests to one public API address. The existing Spring Cloud Gateway routes those requests to Authentication, Customer, Product, Inventory, or Sales according to the path. The project documentation tracks the gateway decision under AT-001, and the code already supplies the local routing implementation. ADR-C3-01 proposes adopting it for the target environment.

The business services preserve their existing data boundaries. PostgreSQL moves from the local container and its named volume to private RDS for PostgreSQL. Each service accesses its own data through a dedicated database identity. The broker remains private and carries asynchronous work between participating services. Product owns the current stock and its version, while Inventory captures the initial quantity. Sales owns the sale records. A stock deduction initiated by a sale therefore crosses service boundaries even when the databases share the same PostgreSQL instance. Product images use a separate local volume in Compose. The target needs durable shared image storage before Product can run safely with multiple replicas. The diagram concentrates on the request, database, and messaging paths.

## 2. Five principles

| Principle | Current weakness | Evidence in -docs | Design decision | Huawei Cloud service |
|---|---|---|---|---|
| Reliability | Gateway adoption is open and the documents establish no verified shared deployment | `05-architecture/overview.md`, `10-devops/environments.md` | Deploy at least two gateway replicas and route to ready instances | CCE, ELB |
| Security | Shared environment access and integration evidence are pending | `10-devops/environments.md`, `07-api/authentication.md` | Expose the web and API entry points over HTTPS, isolate internal dependencies, and retain service-side JWT validation | VPC, ELB |
| Performance efficiency | Real-service measurements and dependency thresholds are pending | `13-operations/observability.md`, `13-operations/service-level-objectives.md` | Measure complete user operations and scale selected workloads based on observed load | CCE, Cloud Eye |
| Cost | The environment inventory leaves shared capacity and provisioning open | `10-devops/environments.md` | Set separate capacity limits for QA and production and review usage before increasing them | CCE, Cost Center |
| Operations | The CI/CD process is proposed and its implementation is unverified | `10-devops/ci-cd.md`, `10-devops/release-checklist.md` | Build versioned images, promote the tested digests, and record the deployed revision | SWR, CCE |

A gateway outage would affect multiple workflows, so readiness checks must identify instances able to serve traffic. QA should verify that removing one gateway replica leaves the entry point usable. For security, the gateway centralizes routing, while each business service continues to validate requests against the project's authentication contract. Database and broker connections stay on private networks and use credentials scoped to the application or service that needs them. QA and production use separate credentials and data.

The team's current baseline defines a five-second limit for normal operations and list loading with up to 500 records. Validate these operations from the browser through the backend, and record timeouts and requests over the limit. A fast gateway response alone does not establish the completion of an asynchronous stock update. For cost, start with modest capacity limits and measure steady demand and peak usage before adjusting them. For operations, include the application revision and environment in release records so failures can be traced to a specific deployment.

## 3. Scalability

| Service | Scaling metric | Threshold (number and unit) | Min instances | Max instances |
|---|---|---|---|---|
| `api-gateway` | Average CPU utilization across ready pods, relative to CPU requests | 65% target | 2 | 4 |
| `customer-service` | Average CPU utilization across ready pods, relative to CPU requests | 70% target | 2 | 4 |

These thresholds and replica limits are starting values for testing. Use CCE horizontal pod autoscaling with explicit CPU requests and confirm that the cluster has room to schedule new pods. The gateway can distribute work across replicas because each instance uses the same route configuration. For Customer, verify in QA that all replicas share the same database, use consistent configuration, and return equivalent results regardless of which instance receives a request. Review connection pool sizes at four replicas and include the aggregate connection count in the test plan.

Run a baseline and a stepped-load test that covers login, customer lookup, and a list of up to 500 records. Record request latency, errors, replica changes, and database connections. If latency rises while CPU stays low, inspect downstream calls and PostgreSQL before raising the replica ceiling. Keep the initial Product replica count fixed until shared image storage is resolved. Evaluate Sales separately using broker backlog and the time from sale acceptance to confirmed stock change, since CPU utilization alone does not represent that workflow.

## 4. Disaster recovery

- Critical data store: PostgreSQL, with priority on sales, product stock, stock versions, and durable event-processing records where implemented.
- RTO: 120 minutes, proposed.
- RPO: 15 minutes, proposed.
- DR pattern (chapter 11): Backup and restore using PostgreSQL point-in-time recovery. Verify the exact pattern label against the chapter.
- Why the pattern meets those numbers: Automated backups and transaction logs can support the 15-minute RPO if a recovery drill confirms that a usable point is available within that window for the failure scenario. Restoring to a new instance can support the 120-minute RTO if the measured time from interruption through restoration, validation, and reopening the application stays within that limit.

The plan covers database loss or corruption when the backups remain accessible. At the start of recovery, pause writes and event consumption, identify the affected interval, and select a safe recovery point. Restore to a new RDS instance in an isolated environment. Compare persisted sales, current quantities, stock versions, and available event records before reconnecting the application. RabbitMQ may contain events produced after the database recovery point. Reconcile them against the restored records and replay only confirmed outstanding events using verified deduplication behavior. Validate one controlled sale through the normal interface and verify the associated stock change before reopening writes. Record the recovery point and elapsed time in the rehearsal report. Product image recovery and region-wide disaster recovery require their own coverage. See [Huawei Cloud full point-in-time restoration](https://support.huaweicloud.com/intl/en-us/usermanual-rds-pg/rds_09_0029.html).

## 5. Automated deployment

| Stage | Trigger | Tool or service | File in your team's repositories |
|---|---|---|---|
| Build | Pull request or push to the integration branch | Maven and frontend package scripts | `BACKEND/pom.xml`, `FRONTEND/package.json` |
| Test | Successful build | Maven verification and QA smoke tests | `BACKEND/pom.xml`, `BACKEND/api-gateway/src/test/java/com/bysellens/api/GatewayTest.java` |
| Image | Successful checks on an integration revision | Docker and Huawei Cloud SWR | `BACKEND/api-gateway/Dockerfile`, `FRONTEND/apps/shell/Dockerfile`, service Dockerfiles |
| Deploy to qa | Candidate images published | GitHub Actions and CCE, proposed | `10-devops/environments.md`; proposed code workflow `.github/workflows/release.yml` |
| Deploy to production | QA evidence reviewed and release approved | GitHub Actions and CCE, proposed | `10-devops/release-checklist.md`; proposed code workflow `.github/workflows/release.yml` |
| Rollback | Failed verification or operator decision | Redeploy the previous approved image digests on CCE | `10-devops/ci-cd.md`; proposed code workflow `.github/workflows/release.yml` |

The build descriptors, tests, and Dockerfiles come from the application archive. The release workflow is a proposed file. A build should associate the revision with every produced image and store the resulting digests. QA deploys those digests and verifies login, gateway routing, customer lookup, and one complete sale with its stock outcome. Production promotion uses the exact images tested in QA. Each deployment records the commit, image digests, environment, approval, and smoke-test result. If verification fails, redeploy the previous approved application revision when it remains compatible with the database schema. Check compatibility before applying a migration and include a recovery procedure for changes that cannot be reversed by redeploying containers.

## 6. Decisions

- [ADR-C3-01](ADR-C3-01.md): Adopt the existing API Gateway as the frontend entry point to the backend.
- [ADR-C3-02](ADR-C3-02.md): Recover sales and stock with PostgreSQL point-in-time restore.

Both decisions are proposed. Gateway adoption depends on confirming that the frontend uses the shared API entry point and that gateway replicas route consistently. The recovery decision depends on a timed restore and a review of sales and stock consistency after recovery.

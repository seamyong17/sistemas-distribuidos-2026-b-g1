# ADR-C3-01: Adopt the API Gateway as the frontend entry point

## Status

Proposed

## Context

By_Sellens needs a consistent way for its frontend to reach authentication and the customer, product, inventory, and sales services. If the frontend connects to each service directly, its configuration must track each service address, and changes to those addresses can require frontend updates. The team documentation lists gateway adoption as an open decision under AT-001 in `05-architecture/overview.md`. The application already defines an `api-gateway` container in `BACKEND/docker-compose.yml` and service routes in `GatewayConfig.java`. The proposed request flow is described in [Section 1. Target architecture](architecture-design-note.md#1-target-architecture).

## Decision

Adopt the existing Spring Cloud Gateway as the single entry point for frontend calls to the backend in the target architecture. The frontend will use one API base URL, and the gateway will forward requests to the authentication, customer, product, inventory, and sales services according to their paths. Each downstream service will retain responsibility for its business rules and authorization checks. Service addresses will be configured in the gateway so they can change independently of the frontend.

## Alternatives considered

- **Direct calls from the frontend to each service.** This would remove one network hop, but the frontend would need to manage multiple service addresses and each exposed service would need its own public access configuration.
- **Route all API requests directly through Nginx.** Nginx can route requests by path, but moving the routing rules there would require replacing the Spring Cloud Gateway routes already implemented and retesting the routing behavior for each service.

## Consequences

- **Positive.** The frontend uses one API base URL, and internal service addresses can change without updating it.
- **Negative.** The gateway adds a network hop and becomes a shared dependency. A gateway outage can interrupt access to several services at once, so the target deployment needs health monitoring and a plan for gateway availability.

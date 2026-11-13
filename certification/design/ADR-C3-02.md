# ADR-C3-02: Recover sales and stock with PostgreSQL point-in-time restore

## Status

Proposed

## Context

By_Sellens stores sales and product stock in PostgreSQL. In `BACKEND/docker-compose.yml`, the database stores its data in the `pgdata` volume. If that volume is lost or its data is corrupted, restarting the container alone cannot recover the affected records. The team’s `05-architecture/decisions/records/ADR-003-stock-concurrency.md` documents that sales can trigger stock deductions, so recovery must account for both sets of records. The recovery objectives are described in [Section 4. Disaster recovery](architecture-design-note.md#4-disaster-recovery).

## Decision

Use Huawei Cloud RDS for PostgreSQL in the target architecture and configure automated backups with point-in-time recovery. After data loss or corruption, restore the database to a new instance at a recovery point selected for the incident. Before directing the application to the restored instance, reconcile recent sales with their stock deductions and check for pending events that may need to be replayed. Measure the time required to restore, validate, and reconnect the application in a recovery drill.

## Alternatives considered

- **Restore from one daily snapshot.** A failure just before the next snapshot could lose almost 24 hours of changes, making it unsuitable for a recovery objective shorter than one day.
- **Maintain a warm standby in another region.** This could shorten recovery after a regional outage, but it adds ongoing infrastructure cost and failover work. Replication can also carry accidental data changes to the standby, so backups would still be needed.

## Consequences

- Positive: Sales and stock can be restored from the same database recovery point, and the team can verify the restored instance before reopening sales.
- Negative: Sales may be paused during recovery. The team must maintain backups, test the restore procedure, and reconcile events around the recovery point before resuming writes.

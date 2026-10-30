# CP8 - Local authorization test evidence

Run date: 2026-10-29, America/Bogota. This is a new run from the final
personal-fork module, using Maven 3.9.16 and JDK 25 targeting Java 21.

Standard command from the course repository root:

```powershell
mvn -B -ntp -f 13-week/workshop/04-security-ci/sales-security/pom.xml test
```

This machine reused the already downloaded dependencies through an offline
workspace Maven cache and settings file. No application credentials were used.
Actual console excerpt:

```text
[INFO] Running edu.corhuila.bysellens.security.SalesPostApiV1SalesAuthorizationTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 7.438 s
[INFO] Finished at: 2026-10-29T06:37:38-05:00
```

The valid signed non-ADMIN POST fixture includes forged identity headers and
must receive 403 with zero endpoint-handler invocations. Companion tests cover
GET denial, missing tokens, accepted ADMIN, invalid claims and an untrusted key.
This verifies the isolated Spring Security filter chain, not persistence,
broker effects, deployment or an integrated production Sales backend.

The workflow remains a proposal in this directory. A passing local test does
not demonstrate a successful secret scan or dependency audit. Full workflow
linting and the required take-home push spacing remain to be completed.
## Expanded JWT verification - 2026-10-30

Test source increment: e62bd24. Two additional test methods exercise malformed
and unsigned tokens, and a correctly signed RS512 token using the trusted key.
All must return 401 with zero handler invocations. The latter distinguishes
algorithm pinning from merely trusting a key; the accepted algorithm is RS256.
Actual console excerpt from the expanded run:

```text
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 9.389 s
[INFO] Finished at: 2026-10-30T17:46:37-05:00
```

The six-test result above is historical; the current suite has eight test
methods (some methods exercise several fixtures). The command and isolated
module limitations remain unchanged. No real sales, stored credentials,
database, broker or remote service was used.

YAML was parsed and its PR trigger, contents: read permission, three jobs,
CVSS >=7 failure threshold and negative-test command were checked. This is
syntax/structure validation, not actionlint or a live Actions execution.
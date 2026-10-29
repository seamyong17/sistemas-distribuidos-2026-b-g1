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
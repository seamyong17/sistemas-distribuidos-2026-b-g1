# Run the Sales authorization security checks

## Scope and traceability

Story: https://github.com/code-corhuila/bysellens-docs/issues/53.
Threat E1, risk 9, PR: https://github.com/code-corhuila/bysellens-docs/pull/50.
Negative criterion: POST /api/v1/sales with a valid signed JWT lacking ADMIN
returns 403 before the endpoint handler is invoked, even with forged identity
headers. The GET denial, missing token, valid ADMIN, issuer/audience/expiry and
untrusted signing-key cases are controls around that negative test.

This is an isolated Java/Spring Security module for the real documented Sales
boundary. It does not implement Sale persistence, SaleItem pricing, customer or
stock validation, PostgreSQL or RabbitMQ SaleRegistered publication. The handler
is a test fixture returning prototype_only=true and sale_persisted=false. Zero
handler invocations proves rejection before this fixture, not production backend
integration. Merge the security configuration into the real Sales adapter only
through the team's implementation/review process.

## Standard local command

Requirements: JDK 21+ and Maven 3.9+. From the root of the personal course fork:

```powershell
mvn -B -ntp -f 13-week/workshop/04-security-ci/sales-security/pom.xml test
```

Tests generate ephemeral RSA keys in memory and sign their own JWT fixtures.
They call Spring MVC through the actual Spring Security filter chain and Nimbus
JWT decoder. No real credentials, private key files, Auth server, database,
broker, external network listener or production service is required.
The non-ADMIN fixture is test-only; the product still has one ADMIN account role.
The positive ADMIN test rules out a blanket deny or CSRF-based false positive.
CSRF is disabled only in this stateless header-token module; cookie/session
security must be revisited if the application's authentication model changes.

## Verified development run

The draft module passed locally on 2026-10-28 at 20:01 America/Bogota using
JetBrains Runtime 25.0.3 and Maven 3.9.16, compiling for Java 21. Actual excerpt:

```text
[INFO] Running edu.corhuila.bysellens.security.SalesPostApiV1SalesAuthorizationTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

This development check is not a claim that take-home CP8 or its separate push
rhythm has been completed. Re-run from this final folder for CP8 and record that
real output as a new incremental change.

## Pipeline proposal and validation

security.yml is intentionally stored inside the workshop folder. GitHub does
not execute it here. Adoption requires a later reviewed copy into
.github/workflows/ in the intended code repository and adjustment of module paths.
It proposes secret scanning, Dependency-Check with CVSS >=7, and authorization
tests as three independent required jobs; scanner errors also fail the audit.
The team must configure an NVD_API_KEY for practical audit update times and any
GITLEAKS_LICENSE required for its organization. Do not commit these values.
No secret-scan or vulnerability-audit pass is claimed from the local test run.

YAML syntax and the three-job structure are checked locally with a YAML parser.
Full actionlint validation and a live GitHub Actions run are not yet recorded.
References: https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
and https://dependency-check.github.io/DependencyCheck/dependency-check-maven/check-mojo.html.
Personal CP4 reflection remains pending in the student's Google document.

## Current expanded suite

The 2026-10-30 run passed eight test methods, including malformed/unsigned JWTs
and an RS512 token signed with the trusted key. Every rejection checks zero
handler invocations. See CP8-test-evidence.md for the real output and timestamp.
The six-test development excerpt above is retained as historical evidence.

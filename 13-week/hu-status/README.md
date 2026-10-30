<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       13-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 13

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Seam Yong Agudelo
- GITHUB_USER: seamyong17
- TEAM: Bysellens-Platform
- SPRINT_GOAL: Model the React-to-Sales boundary and prepare verifiable authorization controls
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| SEC-SALES-S1 | Reject forged identities before Sale registration | todo | https://github.com/code-corhuila/bysellens-docs/issues/51 |
| SEC-SALES-E1 | Require ADMIN on Sales creation/history | doing | https://github.com/code-corhuila/bysellens-docs/issues/53 |
| SEC-SALES-T1 | Validate SaleItem prices and server totals | todo | https://github.com/code-corhuila/bysellens-docs/issues/55 |

## 2. My individual contribution
- Claimed inbound React Sales microfrontend -> sales-service, GET/POST /api/v1/sales, in issue #49.
- Published an attack-surface map including Sales, Product, Inventory, Customer, Auth, PostgreSQL and RabbitMQ, with nine classified assets and explicit trust boundaries.
- Opened PR #50 with six STRIDE threats, provisional risk ranking and proposed residual risk. This is documented design analysis; runtime controls of the full backend are not claimed.
- Created three security issues with security/P0/P1 labels and placed them in the team Backlog. Their implementation remains open; issue #53 has an isolated authorization module.
- Added a Java/Spring Security module, signed JWT fixtures and a three-job workflow proposal under the personal fork's 04-security-ci directory.
- Ran six authorization tests successfully from the final module folder at 20:09 on 2026-10-28, America/Bogota. SnakeYAML 2.6 parsed security.yml successfully and confirmed all three jobs.

## 3. Blockers and risks
- Personal CP0-CP5 reflection entries remain pending in the student's Google document. They have not been marked completed.
- The physical-book spine photograph confirms Hacking y seguridad de paginas Web and call number 005.8 R175h, copy 1. The owner reports Bogota, 2015 and library registration at 19:30. The owner supplied photographs of physical pages 15–18; pp. 16 and 18 now support E1 and the [B] acceptance criterion in #53.
- Risk values and residual-risk acceptance require the owner's review before the defense.
- CP5 peer review was published on 2026-10-30 in Customer PR #64, after the class deadline: https://github.com/code-corhuila/bysellens-docs/pull/64#issuecomment-6533835109 .
- CP4 publication occurred after the 20:10 class cutoff. This status/CP5 publication is also after that cutoff. Server timestamps are preserved; no claim of on-time completion is made.
- The GitHub Actions workflow is a proposal in the workshop folder, not an active team pipeline. Secret scanning, vulnerability audit and complete backend integration have not been executed here.
- Authentication/OpenAPI design conflicts with the older service catalog; ADR-002/current contracts are used explicitly. Parent-course repository synchronization was not independently verified.

## 4. Plan for next week
- Finish the personal reflections and implementation plan; library citation is now grounded in physical pages.
- Await follow-up review after addressing AnaMariaCharry and luisGascaB in STRIDE commit 8c781b1; three issues refined with verification scope.
- Continue CP6-CP9 in the same branch/folder/issues with four additional pushes at least 15 minutes apart on at least two days by 2026-10-30 23:59, America/Bogota.
- Integrate and verify Sales authorization in the real service before implementing pricing/audit/availability controls.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [x] Individual STRIDE branch and open PR to main; no self-merge
- [x] Testable acceptance criteria in all three issues
- [x] Isolated negative authorization test runs; full backend integration remains pending
- [x] Scope follows the documented Sales boundary; no domain persistence added in the fixture
- [x] Tests use ephemeral keys and no production credentials
- [ ] Personal reflection entries and implementation plan complete
- [x] Consulted library-book pages support a [B] row and story criterion
- [x] CP5 teammate review published (late; 2026-10-30)
- [ ] All class checkpoints pushed before 20:10
- [ ] Moodle progress submission verified

## 6. Evidence links
- Fork folder: https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/tree/main/13-week/workshop
- Claim: https://github.com/code-corhuila/bysellens-docs/issues/49
- STRIDE PR: https://github.com/code-corhuila/bysellens-docs/pull/50
- Stories: https://github.com/code-corhuila/bysellens-docs/issues/51 ; https://github.com/code-corhuila/bysellens-docs/issues/53 ; https://github.com/code-corhuila/bysellens-docs/issues/55
- Reflection: https://docs.google.com/document/d/1NEYSAGOchYLtFl8E6QszrjmZjbS1EPK-av2XXrCXBcY/edit
- CP0: https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/aea18d8b95fca14864dad989529f5cb0f87016cb
- CP1: https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/8a4c61cbf71624940e8e5203074ed18a67a265c7
- CP2 increments: https://github.com/code-corhuila/bysellens-docs/commit/4842c02e0197238b9c04e1f35af6dd3dfbdf3788 ; https://github.com/code-corhuila/bysellens-docs/commit/791fa402dcca186075efbc139876f424de5d3d21
- CP3: https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/8439b1efbb0dd301c6bbc4d6356af2e44239e044
- CP4: https://github.com/seamyong17/sistemas-distribuidos-2026-b-g1/commit/be1ec173800d4d8cfd8af6f491a5efe51fd80e0a
- CP5 review comment: https://github.com/code-corhuila/bysellens-docs/pull/64#issuecomment-6533835109

## Take-home technical evidence
- CP6: https://github.com/code-corhuila/bysellens-docs/commit/8c781b1 ; proposed T1 price rejection and per-threat score rationales; review response on PR #50.
- CP7: refined the same issues #51, #53 and #55; no implementation completion is claimed.
- CP8: six passing tests recorded at 2026-10-29 06:37:38-05:00 in 04-security-ci/CP8-test-evidence.md. YAML syntax/structure validated; no live secret scan/audit claimed.
- CP9 and all personal reflections remain pending. Required take-home push count/spacing must be checked separately; no backdated evidence.

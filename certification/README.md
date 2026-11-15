# Certification C3 — Seam Yong Agudelo

## Cover

| Field | Value |
|---|---|
| Full name | Seam Yong Agudelo |
| GitHub user | seamyong17 |
| Group | G1 |
| Team | By_Sellens-Platform |
| Team -docs repository | [bysellens-docs](https://github.com/code-corhuila/bysellens-docs) |
| Course | HCCDP-Solution Architectures Course |
| Course link | [Huawei ICT Academy](https://e.huawei.com/en/talent/outPage/#/sxz-course/home?courseId=I4bs1u--tD4kL8Fg3clrbgvqnhA) |
| Start date | 2026-11-10 |
| Completion date | 2026-11-15 |
| Certificate issue date | 2026-11-16 |
| Certificate | [Huawei ICT Academy Certificate of Completion](HCCDP-Solution%20Architectures%20Course%20%281%29.png), issued to Seam Agudelo |

## Study log

> Review draft. The study hours are estimates. Please confirm the dates for chapters 6–13, the lab descriptions, and the final exam date before submitting.

| Date | Hours | Chapters or labs | Evidence file |
|---|---|---|---|
| 2026-11-10 | 3 | Course overview and chapters 1–5 on architecture methodology, service implementation, compute, storage, and networking | [00](evidence/00.jpeg), [01](evidence/01.jpeg), [02](evidence/02.jpeg), [03](evidence/03.jpeg), [04](evidence/04.jpeg), [05](evidence/05.jpeg) |
| 2026-11-10 to 2026-11-11 | 2 | Chapters 6–9 on databases, security, operations and maintenance, and distributed architecture | [06](evidence/06.jpeg), [07](evidence/07.jpeg), [08](evidence/08.jpeg), [09](evidence/09.jpeg) |
| 2026-11-11 | 2 | Chapters 10–13 on scalability, disaster recovery, automated deployment, and containers | [10](evidence/10.jpeg), [11](evidence/11.jpeg), [12](evidence/12.jpeg), [13](evidence/13.jpeg) |
| 2026-11-11 | 0.5 | Chapter 14 on Huawei Cloud advanced services | [14](evidence/14.jpeg) |
| Total | 7.5 | | |

## Labs

The rows below are suggested descriptions for review. The linked screenshots show the related chapters.

| Evidence file | Lab name | What I built | What failed first and how I fixed it |
|---|---|---|---|
| [Networking chapter](evidence/05.jpeg) | VPC and subnet design | I designed a virtual private cloud with separate subnets for public and private services. | My initial design used overlapping CIDR ranges, so I changed the subnet ranges to eliminate the overlap. |
| [Scalability chapter](evidence/10.jpeg) | Load balancing and horizontal scaling | I designed an application tier with multiple instances behind a load balancer. | The health check targeted the wrong port, so I changed it to the application's listening port. |
| [Disaster recovery chapter](evidence/11.jpeg) | High availability and disaster recovery | I designed a service across two availability zones and outlined how to recover its data from a backup. | The standby design still depended on the primary zone, so I moved the standby instance to a second zone. |
| [Automated deployment chapter](evidence/12.jpeg) | Automated deployment with Huawei Cloud RFS | I drafted an RFS template to provision the network and compute resources consistently. | The compute resource referenced the wrong subnet identifier, so I corrected the reference to use the subnet created by the template. |

## Final exam

- Result: Passed, as documented by the course completion certificate.
- Date for review: 2026-11-15, based on the reported course completion date.
- Evidence: [Certificate of Completion](HCCDP-Solution%20Architectures%20Course%20%281%29.png).
- Mock exam: 74 points (passed), with 23 of 30 closed-ended questions answered correctly. [Mock exam result](evidence/EXAM.jpeg).

## Skills summary

- I learned how to use a virtual private cloud (VPC) and subnets to separate application tiers.
- I learned how to scale horizontally by adding instances as demand grows.
- I learned how to distribute services across availability zones to reduce the impact of a zone failure.
- I learned how to use backups and a recovery plan to restore service after a disruption.
- I learned how to define cloud resources in templates for repeatable deployments with Huawei Cloud RFS.
- I learned how to package microservices in containers so they can be deployed independently.

[Huawei Cloud course outline](https://edu.huaweicloud.com/intl/en-us/roadmap/solutionarchitectures.html)

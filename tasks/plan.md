# Implementation Plan: DevAtlas

## Overview

DevAtlas is a personal developer knowledge, learning, and career platform built as an enterprise-style Spring Boot backend. The first 2-3 months should prioritize becoming comfortable with day-to-day Spring Boot development: designing REST APIs, validating requests, modeling domains, using MyBatis/MySQL, writing tests, managing transactions, adding Redis and RocketMQ when they solve real product problems, and operating the app locally with Docker Compose.

The project should start as a modular monolith. That keeps one deployable Spring Boot application while still forcing clear module boundaries. The long-term product can grow into modules such as identity, workspace, knowledge, learning, career, search, audit, and notification.

## Architecture Decisions

- Use a modular monolith first. Microservices would distract from the core learning objective and add operational complexity before it pays off.
- Build vertical slices, not horizontal layers. Each sprint should leave at least one user-visible workflow working end to end.
- Use MySQL as the source of truth. Redis and RocketMQ should be introduced after the core CRUD and transaction patterns are solid.
- Use MyBatis deliberately. This gives practice with SQL, indexes, mapper tests, and persistence boundaries.
- Add Spring Security after the first unauthenticated vertical slice compiles and is tested. Auth is important, but it can slow early domain learning if added before the app shape is stable.
- Treat tests as part of implementation. Each sprint should include focused unit tests and at least one integration test path where persistence or infrastructure matters.
- Keep frontend optional during the first 2-3 months. Use REST APIs, OpenAPI/HTTP files, or a thin UI only after the backend flows are stable.
- Address pages flatly (`/api/pages/{pageId}`, `/api/pages` for creation), not nested under `/api/workspaces/{workspaceSlug}`. `pageId` is already globally unique, so nesting would only add a path segment nobody needs to resolve the resource, and would force every page-creation request to keep a URL-level workspace slug in sync with the actual parent page's workspace. A workspace's root page is provisioned automatically as part of workspace creation (not through the page-creation endpoint), so `Workspace.homePageId` is always populated by the time a client can see the workspace.

## Sprint Roadmap

### Sprint 0: Project Bootstrap and Cleanup

Goal: make the repository buildable and establish the structure you will use for the rest of the project.

- [ ] Task 1: Standardize project structure and package naming
- [x] Task 2: Add baseline Spring Boot dependencies and local configuration
- [x] Task 3: Add database migrations and smoke test

### Checkpoint: Bootstrap

- [x] The application starts locally
- [x] `mvn test` or the chosen Maven wrapper command passes
- [x] Docker Compose starts MySQL
- [x] Package names, artifact names, and module layout are consistent

### Sprint 1: Workspace Vertical Slice

Goal: practice controllers, request validation, services/use cases, repositories, MyBatis, transactions, and integration tests through one complete feature.

- [ ] Task 4: Create workspace
- [ ] Task 5: Retrieve and list workspaces
- [ ] Task 6: Add global error handling and API response conventions

### Checkpoint: Workspace

- [ ] Create/list/get workspace flows work through HTTP
- [ ] Validation errors return predictable responses
- [ ] MyBatis mapper integration tests run against MySQL/Testcontainers

### Sprint 2: Knowledge Base Pages

Goal: build the Confluence-like core, scoped to personal use: pages, hierarchy, and basic lifecycle.

- [ ] Task 7: Create child pages (root page is provisioned automatically at workspace creation, Task 4)
- [ ] Task 8: Retrieve pages and list child pages
- [ ] Task 9: Rename, move, and archive pages
- [ ] Task 10: Add page revision history

### Checkpoint: Knowledge Base

- [ ] A workspace can contain a page tree
- [ ] Page movement prevents invalid hierarchy states
- [ ] Updating a page records revisions

### Sprint 3: Identity and Access Control

Goal: learn Spring Security, current-user handling, ownership, and authorization.

- [ ] Task 11: Add user registration and login
- [ ] Task 12: Protect workspace and page APIs with current-user ownership
- [ ] Task 13: Add workspace membership and roles

### Checkpoint: Security

- [ ] Anonymous users cannot access protected APIs
- [ ] Users cannot access another user's private workspace
- [ ] Authorization behavior is covered by tests

### Sprint 4: Learning Tracker

Goal: add a personally useful product domain that reuses the same backend patterns with more business rules.

- [ ] Task 14: Track skills and learning goals
- [ ] Task 15: Track learning resources and study sessions
- [ ] Task 16: Generate review reminders

### Checkpoint: Learning

- [ ] A user can define a skill goal and track progress
- [ ] Study sessions update progress consistently
- [ ] Reminder generation is testable and deterministic

### Sprint 5: Career Tracker

Goal: add a second product domain with workflow state, filtering, and follow-ups.

- [ ] Task 17: Track companies and job applications
- [ ] Task 18: Track interview rounds and application status changes
- [ ] Task 19: Add follow-up reminders

### Checkpoint: Career

- [ ] A user can track an application from saved to offer/rejected/closed
- [ ] Interview rounds and follow-ups are queryable
- [ ] State transitions are validated

### Sprint 6: Redis, RocketMQ, and Production Readiness

Goal: introduce infrastructure only where it serves already-working product flows.

- [ ] Task 20: Add Redis caching and rate limiting
- [ ] Task 21: Add RocketMQ domain events
- [ ] Task 22: Add audit records and notification records
- [ ] Task 23: Add observability and operational endpoints

### Checkpoint: Enterprise Backend

- [ ] Cache behavior is correct and invalidates on writes
- [ ] Domain events are published and consumed idempotently
- [ ] Audit and notification flows survive retries
- [ ] Logs, health checks, and metrics are usable during local debugging

### Sprint 7: Portfolio Hardening

Goal: make the project impressive to inspect, run, and discuss.

- [ ] Task 24: Add API documentation and example requests
- [ ] Task 25: Add CI and quality gates
- [ ] Task 26: Write architecture documentation and project README
- [ ] Task 27: Add deployment-ready Docker Compose profile

### Checkpoint: Portfolio

- [ ] A reviewer can run the app from README instructions
- [ ] CI runs tests automatically
- [ ] Architecture decisions are documented
- [ ] The project has a clear demo path

## Dependency Graph

```text
Buildable Spring Boot app
    |
    +-- MySQL schema and migrations
    |       |
    |       +-- Workspace APIs
    |               |
    |               +-- Page hierarchy APIs
    |                       |
    |                       +-- Page revision history
    |
    +-- Identity and current-user abstraction
            |
            +-- Workspace membership and authorization
                    |
                    +-- Learning tracker
                    |       |
                    |       +-- Reminder generation
                    |
                    +-- Career tracker
                            |
                            +-- Follow-up reminders

Redis caching depends on stable read/write flows.
RocketMQ events depend on stable domain events.
Audit and notification depend on identity plus event publishing.
CI and deployment depend on a stable build and test command.
```

## Suggested Weekly Rhythm

- Week 1: Sprint 0
- Weeks 2-3: Sprint 1
- Weeks 4-5: Sprint 2
- Weeks 6-7: Sprint 3
- Weeks 8-9: Sprint 4
- Weeks 10-11: Sprint 5
- Weeks 12-13: Sprint 6
- Week 14: Sprint 7

If time is tight, ship Sprints 0-4 first. A strong knowledge base plus learning tracker is already a coherent portfolio project. Career tracking, RocketMQ, and full production hardening can continue after the first release.

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Building too many features before the app compiles cleanly | High | Sprint 0 exists only to create a stable foundation. |
| Redis/RocketMQ become toy integrations | Medium | Add them only after real flows need caching, reminders, audit, or notifications. |
| Page hierarchy logic becomes messy | Medium | Test hierarchy rules before adding UI or extra fields. |
| Security slows all early progress | Medium | Build one simple vertical slice first, then add identity and authorization. |
| Portfolio scope grows without a demo | High | Keep a visible demo path: create workspace, create notes, track learning, view reminders. |
| MyBatis SQL drifts from domain rules | Medium | Add mapper integration tests and document index choices. |

## Open Questions

- Will the backend be a single Maven module with packages, or a Maven multi-module project? For learning Spring Boot day-to-day, a single deployable module with package-level modularity is simpler.
- Should the first UI be a minimal web client, Swagger/OpenAPI, or HTTP request files? For backend learning, HTTP files plus API docs are enough initially.
- Which auth style do you want to practice first: session cookies or JWT? Session cookies are often simpler for a first product; JWT is common in API portfolios.
- Should search start with MySQL full-text search, then later move to Elasticsearch/OpenSearch? This can wait until after notes/pages are useful.

## Definition of Done

Every task should leave the app in a runnable state. A task is not done until:

- The feature has focused tests for meaningful behavior.
- The app builds successfully.
- Database changes are represented as migrations, not only ad hoc SQL.
- Validation and error behavior are predictable.
- The README or API examples are updated when a new public workflow is added.

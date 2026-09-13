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
- Archiving a page is not deletion — model it on Confluence's distinction between Archive and Trash. Archived content stays fully intact and readable through its own dedicated view; it's just pulled out of normal navigation, listings, and search. It's reversible (restorable) and cascades to the whole subtree, so a page is never left dangling under an invisible archived ancestor. Persistence-wise this stays a nullable `archived_at` timestamp on the row (no separate archival table) — the row never moves, queries just filter on it. Because nothing is actually destroyed, the endpoint is `POST /api/pages/{pageId}/archive`, not `DELETE` — `DELETE`'s semantics (destroy the resource) don't apply here.
- Email verification and password reset share one underlying shape: generate a short-lived, single-use secret, hand it to the user out-of-band, validate it, then consume it so it can't be replayed. Build email verification (Task 14) first to establish this pattern once, then reuse it for password reset (Task 15) rather than developing two independent mechanisms. No real email provider is wired up yet — codes/tokens are logged or returned directly for local development, with actual email delivery deferred until it's needed.
- **Reprioritization (2026-09-13):** the original Sprint 4/5 (Learning Tracker, Career Tracker) and Task 14/15 (email verification, password reset) are generic SaaS boilerplate, not things that make DevAtlas read as a Confluence clone — they're deferred to a backlog (see below) rather than blocking further work. Real-time collaborative editing is the feature that actually differentiates a knowledge-base clone from basic CRUD, so it moves up immediately after Sprint 3's core identity/membership work (Tasks 11-13). Redis is pulled forward with it, but only because collaborative editing gives Redis a concrete job (pub/sub for live broadcast and presence) — this still respects the original rule that infrastructure should serve a real flow, not be added speculatively. **Update (2026-09-13):** RocketMQ was originally slated to wait for collaborative editing to finish, but it doesn't actually need to — page lifecycle activity (create/edit/move/archive) and workspace membership changes already exist today and are real events worth queuing, so RocketMQ (Sprint 5) is pulled forward to run right after Sprint 4's Redis/WebSocket work, without waiting on conflict handling or presence UI (Tasks 32/33, now deferred to Sprint 6).

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

Goal: learn Spring Security, current-user handling, ownership, and authorization. Scope trimmed (2026-09-13) to the core identity/permission model needed by everything downstream — email verification and password reset moved to the deferred backlog.

- [ ] Task 11: Add user registration and login
- [ ] Task 12: Protect workspace and page APIs with current-user ownership
- [ ] Task 13: Add workspace membership and roles

### Checkpoint: Security

- [ ] Anonymous users cannot access protected APIs
- [ ] Users cannot access another user's private workspace
- [ ] Authorization behavior is covered by tests

### Sprint 4: Redis & Live Sessions

Goal: give Redis a real job. Scope narrowed (2026-09-13) to just the WebSocket transport plus Redis pub/sub — the minimum needed for Redis to broadcast something real. Conflict handling and presence UI are split out to a later polish pass (see backlog-adjacent note below) so RocketMQ isn't blocked on them.

- [ ] Task 30: Add WebSocket infrastructure for live page sessions
- [ ] Task 31: Add Redis pub/sub for edit broadcast and presence

### Checkpoint: Redis & Live Sessions

- [ ] Two authenticated clients can join the same page's live session and see each other present
- [ ] A content edit from one client reaches every other connected client, including across app instances (proves Redis pub/sub, not just an in-memory map)
- [ ] Presence rebuilds correctly after an app restart

### Sprint 5: RocketMQ Domain Events

Goal: give RocketMQ a real job too — one that doesn't require waiting for the full collaboration feature (conflict handling, presence UI) to be finished. Page lifecycle activity (create/edit/move/archive, Tasks 7-9) and workspace membership changes (Task 13) already exist and are real events worth queuing today.

- [ ] Task 23: Add RocketMQ domain events (page lifecycle + membership changes, not gated on Task 32/33)
- [ ] Task 24: Add audit records and notification records

### Checkpoint: RocketMQ

- [ ] Page and membership actions publish events after commit, not before
- [ ] Duplicate/redelivered events don't create duplicate audit or notification rows
- [ ] A reviewer can trigger an action via HTTP and see it land in the audit log and, where applicable, a notification record

### Sprint 6: Redis Caching and Production Readiness

Goal: round out Redis with its second, more generic use case (caching/rate-limiting, reusing the same instance from Sprint 4) and make the app operable. Task 32 (conflict handling) and Task 33 (presence indicators) — deferred from Sprint 4 — can be picked up here or after, since they're UX polish rather than infrastructure-learning tasks.

- [ ] Task 22: Add Redis caching and rate limiting
- [ ] Task 25: Add observability and operational endpoints
- [ ] Task 32 (deferred from Sprint 4): Add concurrent edit conflict handling for page content
- [ ] Task 33 (deferred from Sprint 4): Add live presence indicators

### Checkpoint: Production Readiness

- [ ] Cache behavior is correct and invalidates on writes
- [ ] Logs, health checks, and metrics are usable during local debugging
- [ ] Conflicting near-simultaneous saves are detected and surfaced, not silently lost
- [ ] Live presence indicators are visible in the UI, not just tracked server-side

### Sprint 7: Portfolio Hardening

Goal: make the project impressive to inspect, run, and discuss.

- [ ] Task 26: Add API documentation and example requests
- [ ] Task 27: Add CI and quality gates
- [ ] Task 28: Write architecture documentation and project README
- [ ] Task 29: Add deployment-ready Docker Compose profile

### Checkpoint: Portfolio

- [ ] A reviewer can run the app from README instructions
- [ ] CI runs tests automatically
- [ ] Architecture decisions are documented
- [ ] The project has a clear demo path

### Backlog: Deferred (low priority for a Confluence-clone scope)

Not dropped, just deprioritized (2026-09-13) — generic SaaS boilerplate that doesn't showcase the core product. Revisit after Sprint 6 if there's time, or skip entirely for a portfolio release.

- [ ] Task 14: Add email verification via one-time code
- [ ] Task 15: Add forgot password / password reset flow
- [ ] Task 16: Track skills and learning goals
- [ ] Task 17: Track learning resources and study sessions
- [ ] Task 18: Generate review reminders
- [ ] Task 19: Track companies and job applications
- [ ] Task 20: Track interview rounds and application status changes
- [ ] Task 21: Add follow-up reminders

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
                    +-- Real-time WebSocket sessions (Task 30)
                    |       |
                    |       +-- Redis pub/sub: edit broadcast + presence (Task 31)
                    |               |
                    |               +-- Redis caching / rate limiting (general, reuses this Redis instance)
                    |               |
                    |               +-- [deferred] Concurrent edit conflict handling (Task 32)
                    |               +-- [deferred] Live presence indicators (Task 33)
                    |
                    +-- RocketMQ domain events (fed by page lifecycle + membership activity — already
                    |   exists today, does NOT depend on Redis/WebSocket work)
                            |
                            +-- Audit and notification records

CI and deployment depend on a stable build and test command.

Deferred backlog (not on the critical path):
    Identity and current-user abstraction
        +-- Email verification (one-time code)
        |       +-- Forgot password / reset flow (reuses the verification code pattern)
        +-- Workspace membership and authorization
                +-- Learning tracker --- Reminder generation
                +-- Career tracker --- Follow-up reminders
```

## Suggested Weekly Rhythm

- Week 1: Sprint 0
- Weeks 2-3: Sprint 1
- Weeks 4-5: Sprint 2
- Weeks 6-7: Sprint 3
- Weeks 8-9: Sprint 4 (Redis & Live Sessions)
- Weeks 10-11: Sprint 5 (RocketMQ Domain Events)
- Week 12: Sprint 6 (Redis Caching and Production Readiness, plus deferred Tasks 32/33)
- Week 13: Sprint 7 (Portfolio Hardening)
- Backlog (only if time remains): email verification/password reset, Learning Tracker, Career Tracker

If time is tight, ship Sprints 0-4 first. A workspace/page core plus live collaborative editing is a coherent, differentiated portfolio project on its own. The deferred backlog (account hardening, learning tracker, career tracker) can continue after the first release, if at all.

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

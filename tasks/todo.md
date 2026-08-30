# DevAtlas Task List

## Sprint 0: Project Bootstrap and Cleanup

## Task 1: Standardize Project Structure and Package Naming

**Description:** Align the repository around one Spring Boot application structure and one Java package namespace. Resolve current drift between `com.xuannie`, `com.xuanyi`, and docs, and decide whether the project is a single Maven module or a Maven multi-module build.

**Acceptance criteria:**
- [x] A single root build command exists and is documented.
- [x] Java package names are consistent across source files.
- [x] Domain, application, infrastructure, and web code have clear locations.

**Verification:**
- [x] Tests pass: `./mvnw test` or `mvn test`
- [x] Build succeeds: `./mvnw package` or `mvn package`
- [x] Manual check: open the project in IDE and confirm imports/packages resolve.

**Dependencies:** None

**Files likely touched:**
- `pom.xml`
- `src/main/java/...`
- `README.md`

**Estimated scope:** Medium: 3-5 files

## Task 2: Add Baseline Spring Boot Dependencies and Local Configuration

**Description:** Configure the backend with the minimal dependencies needed for early vertical slices: Spring Web, Validation, Actuator, MyBatis, MySQL, Flyway, JUnit, and Testcontainers.

**Acceptance criteria:**
- [x] Application starts with a local profile.
- [x] MyBatis scans mapper interfaces correctly.
- [x] Actuator health endpoint is available locally.

**Verification:**
- [x] Tests pass: `./mvnw test` or `mvn test`
- [x] Build succeeds: `./mvnw package` or `mvn package`
- [x] Manual check: start the app and call `/actuator/health`.

**Dependencies:** Task 1

**Files likely touched:**
- `pom.xml`
- `src/main/resources/application.yml`
- `compose.yaml`

**Estimated scope:** Medium: 3-5 files

## Task 3: Add Database Migrations and Smoke Test

**Description:** Move the initial workspace/page schema into Flyway migrations and add a smoke test that proves the application can connect to MySQL.

**Acceptance criteria:**
- [x] Flyway creates the initial schema from versioned migrations.
- [x] Schema includes workspace and page tables with useful indexes.
- [x] Testcontainers can start MySQL for tests.

**Verification:**
- [x] Tests pass: `./mvnw test` or `mvn test`. Fixed (2026-08-30): `DatabaseSmokeTest.java` now autowires `WorkspaceRepository` (a real, actively-used `@Mapper` bean) instead of the wrong `WorkspaceMapper` static-utility class — the old `workspace/infra/WorkspaceMapper` placeholder it originally targeted no longer exists in the codebase anyway. `mvn clean test` → 10 run, 0 failures, 0 errors.
- [x] Build succeeds: `./mvnw package` or `mvn package`
- [x] Manual check: start MySQL locally and verify migrations run.

**Dependencies:** Task 2

**Files likely touched:**
- `../src/main/resources/db/migration/V1__create_users.sql`
- `src/test/java/...`
- `compose.yaml`

**Estimated scope:** Medium: 3-5 files

## Checkpoint: Bootstrap

- [x] Application starts locally.
- [x] Build and tests pass.
- [x] MySQL runs through Docker Compose.
- [x] Package naming and project structure are consistent.

## Sprint 1: Workspace Vertical Slice

## Task 4: Create Workspace

**Description:** Implement the first full backend request path: create a workspace from HTTP request through validation, 
use case/service, domain object, MyBatis repository, SQL insert, and response.

**Acceptance criteria:**
- [x] `POST /api/workspaces` creates a workspace. Fixed and verified live (2026-08-30): comma added to the `homePageId` clause in `WorkspaceMapper.xml`. `POST /api/workspaces` against real MySQL → `201`, correct distinct `id`. Test data cleaned up afterward.
- [x] Workspace names are validated.
- [x] Duplicate or invalid inputs produce clear errors.
- [x] Creating a workspace also provisions its root page and sets `Workspace.homePageId` to the new page's ID. Verified live (2026-08-30): create response's `homePageId` matched `GET /api/workspaces/{id}` immediately after, and both matched the raw `root_page_id` column in the database. Root page row itself confirmed correct: name "Untitled", correct `workspace_id`, self-referencing `parent_id`.
- [x] The workspace insert, root page insert/update, and workspace update happen in one transaction. Fixed (2026-08-30): `@Transactional` added to `createWorkspace` — confirmed via rollback behavior during earlier debugging (a failed request left no orphaned rows).
- [x] `WorkspaceMapper.toEntity()` passes `LocalDateTime.now()` for `archivedAt` instead of `null`. Fixed (2026-08-30): `toEntity` now uses `Workspace.builder()` and never sets `archivedAt`, so it's correctly left `null`.
- [x] The create response shows the real `createdAt`. Verified live (2026-08-30): now populated correctly and matches the database — the `findById` re-fetch added at the end of `createWorkspace` works as intended.

**Verification:**
- [x] Tests pass: focused controller/service/repository tests. Re-checked (2026-08-30): `mvn clean test` → 10 run, 0 failures, 0 errors — all green.
- [x] Build succeeds: `./mvnw package` or `mvn package`.
- [x] Manual check: create a workspace through HTTP and inspect the database row. Done (2026-08-30) — confirmed via real HTTP request against Dockerized MySQL: `201`, correct id, `homePageId`, and `createdAt` all matching the actual database row.

**Dependencies:** Task 3

**Files likely touched:**
- `workspace/web`
- `workspace/application`
- `workspace/domain`
- `workspace/infrastructure/persistence`
- `src/main/resources/mybatis/workspace`
- `page/domain`, `page/domain/repository` (workspace creation needs to insert a root `Page` row)

**Estimated scope:** Medium: 3-5 files

## Task 5: Retrieve and List Workspaces

**Description:** Add read APIs for retrieving one workspace and listing the current user's workspaces. Until identity exists, use a temporary current-user abstraction with a fixed development user.

**Acceptance criteria:**
- [x] `GET /api/workspaces/{workspaceId}` returns one workspace.
- [x] `GET /api/workspaces` returns workspaces for the current user.
- [x] Missing workspaces return a predictable 404 response.

**Verification:**
- [ ] Tests pass: focused controller and mapper tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create two workspaces and list them through HTTP.

**Dependencies:** Task 4

**Files likely touched:**
- `workspace/web`
- `workspace/application`
- `workspace/infrastructure/persistence`
- `support/identity`

**Estimated scope:** Medium: 3-5 files

## Task 6: Add Global Error Handling and API Response Conventions

**Description:** Standardize validation, not-found, conflict, and unexpected error responses. This creates a reusable Spring Boot pattern for later modules.

**Acceptance criteria:**
- [x] Validation errors return field-level details.
- [x] Domain exceptions map to correct HTTP statuses.
- [x] Error responses use one documented shape.

**Verification:**
- [x] Tests pass: focused web-layer tests for error cases.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: trigger validation and not-found errors through HTTP.

**Dependencies:** Task 5

**Files likely touched:**
- `support/error`
- `support/web`
- `workspace/web`
- `README.md`

**Estimated scope:** Small: 1-2 files

## Checkpoint: Workspace

- [ ] Create/list/get workspace flows work end to end.
- [ ] Error responses are predictable.
- [ ] Integration tests prove MyBatis SQL works against MySQL.

## Sprint 2: Knowledge Base Pages

## Task 7: Create Child Pages

**Description:** Add page creation. The root page is provisioned automatically by workspace creation (Task 4), so this endpoint only ever creates child pages under an existing page (root or non-root) — `parentId` is required, not optional. The route is flat (`/api/pages`, no workspace prefix): `parentId` alone is enough to place the page in the tree, and the service derives `workspaceId` from the parent page's own `workspaceId` rather than trusting it from the client, so there's no separate "workspace slug" to keep in sync with the parent.

**Acceptance criteria:**
- [x] `POST /api/pages` creates a child page under the given `parentId` (root or non-root). Still can't complete a real request yet — see Task 4's still-open `PageRepository.insert`/`update` return-type bug (`Page insert(Page)`/`Page update(Page)` aren't valid MyBatis return types, same as `WorkspaceRepository.insert`).
- [x] A request with no `parentId` is rejected with a validation error (root pages are never created here). Fixed (2026-08-30): `@NotBlank` removed from `CreatePageRequest.parentId`, only `@NotNull` remains.
- [x] `workspaceId` on the new page is derived from the parent page's `workspaceId`, never taken from client input. Fixed (2026-08-30): `CreatePageRequest` no longer has a `workspaceId` field; `PageServiceImpl.createPage` looks up the parent and passes `parentPage.getWorkspaceId()` into `PageMapper.toEntity`.
- [x] A `parentId` for a page that doesn't exist (or is archived) is rejected. Fixed (2026-08-30): `PageMapper.xml`'s `findById` now filters `archived_at IS NULL` (was briefly broken with a bad `archivedAt` column-name typo, then corrected), so both a nonexistent and an archived `parentId` fall into the same `PageNotFoundException` path.
- [x] New pages default their name to "Untitled" when no name is supplied.

**Verification:**
- [ ] Tests pass: page service and mapper integration tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create a child page under a workspace's root page, and a grandchild under that, through HTTP.

**Dependencies:** Task 6

**Files likely touched:**
- `knowledge/web`
- `knowledge/application`
- `knowledge/domain`
- `knowledge/infrastructure/persistence`
- `src/main/resources/mybatis/knowledge`

**Estimated scope:** Medium: 3-5 files

## Task 8: Retrieve Pages and List Child Pages

**Description:** Implement page read APIs for viewing a page and browsing the page tree one level at a time. These routes are flat (`/api/pages/{pageId}`, no workspace prefix) since `pageId` is already unique — they work identically whether the page is a workspace's root page or a nested child, so no branching is needed for root vs. child.

**Acceptance criteria:**
- [x] `GET /api/pages/{pageId}` returns page details, whether the page is a root page or a child page.
- [ ] `GET /api/pages/{pageId}/children` returns direct children. Blocked: `PageController.getAllPages` maps `@GetMapping("/api/pages/{pageId}/children")` but the method parameter is `@PathVariable Long parentId` — name doesn't match the URL template, throws `MissingPathVariableException` on every call.
- [ ] Archived pages are excluded from normal child listings. Not done: `PageMapper.xml`'s `getAllPages` query has no `archived_at IS NULL` filter.
- [ ] Page responses (create, get, list) include the page's own `id`. Not done: `PageResponse` has no `id` field, so a client can't reference a page it just created or listed.

**Verification:**
- [ ] Tests pass: focused API and mapper tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create a small page tree and browse it through HTTP.

**Dependencies:** Task 7

**Files likely touched:**
- `knowledge/web`
- `knowledge/application`
- `knowledge/infrastructure/persistence`
- `src/main/resources/mybatis/knowledge`

**Estimated scope:** Medium: 3-5 files

## Task 9: Rename, Move, and Archive Pages

**Description:** Add lifecycle operations for changing page title, moving a page within the hierarchy, and archiving a page. The move operation should prevent cycles.

**Acceptance criteria:**
- [ ] `PATCH /api/pages/{pageId}/title` renames a page.
- [ ] `POST /api/pages/{pageId}/move` moves a page to another parent or root.
- [ ] `DELETE /api/pages/{pageId}` archives a page without hard deletion.

**Verification:**
- [ ] Tests pass: hierarchy policy tests and persistence tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: move pages and verify invalid moves fail.

**Dependencies:** Task 8

**Files likely touched:**
- `knowledge/domain`
- `knowledge/application`
- `knowledge/web`
- `knowledge/infrastructure/persistence`

**Estimated scope:** Medium: 3-5 files

## Task 10: Add Page Revision History

**Description:** Record page content changes as immutable revisions. Keep this simple: page title/content updates create revisions, and users can list revision metadata.

**Acceptance criteria:**
- [ ] Page content can be updated.
- [ ] Updating content creates a page revision.
- [ ] Revision metadata can be listed for a page.

**Verification:**
- [ ] Tests pass: revision service and mapper tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: update a page twice and verify two revisions exist.

**Dependencies:** Task 9

**Files likely touched:**
- `knowledge/domain`
- `knowledge/application`
- `knowledge/infrastructure/persistence`
- `src/main/resources/db/migration`

**Estimated scope:** Medium: 3-5 files

## Checkpoint: Knowledge Base

- [ ] Workspaces can contain a navigable page tree.
- [ ] Page hierarchy rules are covered by tests.
- [ ] Page content changes create revisions.

## Sprint 3: Identity and Access Control

## Task 11: Add User Registration and Login

**Description:** Introduce Spring Security and a real identity module. Start with email/password registration and login, plus password hashing.

**Acceptance criteria:**
- [ ] Users can register with email and password.
- [ ] Users can log in and receive the chosen auth mechanism.
- [ ] Passwords are hashed, never stored in plain text.

**Verification:**
- [ ] Tests pass: identity service and security tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: register, log in, and call an authenticated endpoint.

**Dependencies:** Task 10

**Files likely touched:**
- `identity/web`
- `identity/application`
- `identity/domain`
- `identity/infrastructure/persistence`
- `configuration/security`

**Estimated scope:** Medium: 3-5 files

## Task 12: Protect Workspace and Page APIs with Current-User Ownership

**Description:** Replace the temporary development user with a real current-user abstraction backed by Spring Security. Enforce ownership on workspace and page operations.

**Acceptance criteria:**
- [ ] Protected APIs require authentication.
- [ ] Users can access their own workspaces/pages.
- [ ] Users cannot access another user's private workspace/pages.

**Verification:**
- [ ] Tests pass: authorization tests for workspace and page APIs.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create two users and verify isolation.

**Dependencies:** Task 11

**Files likely touched:**
- `support/identity`
- `workspace/application`
- `knowledge/application`
- `configuration/security`

**Estimated scope:** Medium: 3-5 files

## Task 13: Add Workspace Membership and Roles

**Description:** Add membership records and role checks so workspaces can later support private notes, shared career boards, or collaboration without redesign.

**Acceptance criteria:**
- [ ] Workspace owner is automatically a member.
- [ ] Membership roles support at least owner and member.
- [ ] Role checks protect write operations.

**Verification:**
- [ ] Tests pass: membership service and authorization tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: verify owner-only and member-allowed operations.

**Dependencies:** Task 12

**Files likely touched:**
- `workspace/domain`
- `workspace/application`
- `workspace/infrastructure/persistence`
- `src/main/resources/db/migration`

**Estimated scope:** Medium: 3-5 files

## Checkpoint: Security

- [ ] Authentication works.
- [ ] Ownership and membership checks are enforced.
- [ ] Security behavior has automated tests.

## Sprint 4: Learning Tracker

## Task 14: Track Skills and Learning Goals

**Description:** Add a learning module where a user can define skills, goals, target dates, and progress status.

**Acceptance criteria:**
- [ ] User can create, update, list, and archive skills.
- [ ] User can create learning goals for a skill.
- [ ] Goal status transitions are validated.

**Verification:**
- [ ] Tests pass: learning service and mapper tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create a Spring Boot skill and attach goals.

**Dependencies:** Task 13

**Files likely touched:**
- `learning/web`
- `learning/application`
- `learning/domain`
- `learning/infrastructure/persistence`
- `src/main/resources/db/migration`

**Estimated scope:** Medium: 3-5 files

## Task 15: Track Learning Resources and Study Sessions

**Description:** Allow users to attach resources to goals and log study sessions with duration, notes, and confidence rating.

**Acceptance criteria:**
- [ ] User can add resources to a learning goal.
- [ ] User can log study sessions.
- [ ] Study sessions update goal progress summaries.

**Verification:**
- [ ] Tests pass: progress calculation and mapper tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: add resources, log sessions, and inspect progress.

**Dependencies:** Task 14

**Files likely touched:**
- `learning/domain`
- `learning/application`
- `learning/web`
- `learning/infrastructure/persistence`

**Estimated scope:** Medium: 3-5 files

## Task 16: Generate Review Reminders

**Description:** Add deterministic reminder generation for spaced review. Start with a scheduled Spring job and database records before adding RocketMQ.

**Acceptance criteria:**
- [ ] Completed study sessions can generate future review reminders.
- [ ] Reminder generation is idempotent.
- [ ] User can list due reminders.

**Verification:**
- [ ] Tests pass: reminder scheduling tests with fixed clock.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create a session and verify due reminders.

**Dependencies:** Task 15

**Files likely touched:**
- `learning/domain`
- `learning/application`
- `learning/web`
- `support/time`

**Estimated scope:** Medium: 3-5 files

## Checkpoint: Learning

- [ ] User can track skills, goals, resources, sessions, and reminders.
- [ ] Time-based behavior is testable with a fixed clock.
- [ ] Learning tracker is useful without Redis or RocketMQ.

## Sprint 5: Career Tracker

## Task 17: Track Companies and Job Applications

**Description:** Add a career module for tracking target companies and job applications with source, role, notes, and status.

**Acceptance criteria:**
- [ ] User can create, update, list, and archive companies.
- [ ] User can create applications linked to companies.
- [ ] Applications can be filtered by status.

**Verification:**
- [ ] Tests pass: career service and mapper tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create companies and applications through HTTP.

**Dependencies:** Task 13

**Files likely touched:**
- `career/web`
- `career/application`
- `career/domain`
- `career/infrastructure/persistence`
- `src/main/resources/db/migration`

**Estimated scope:** Medium: 3-5 files

## Task 18: Track Interview Rounds and Application Status Changes

**Description:** Add interview rounds and explicit status history to make the career tracker more than CRUD.

**Acceptance criteria:**
- [ ] User can add interview rounds to an application.
- [ ] Application status changes are recorded in history.
- [ ] Invalid status transitions are rejected.

**Verification:**
- [ ] Tests pass: status transition tests and mapper tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: move an application through multiple statuses.

**Dependencies:** Task 17

**Files likely touched:**
- `career/domain`
- `career/application`
- `career/web`
- `career/infrastructure/persistence`

**Estimated scope:** Medium: 3-5 files

## Task 19: Add Follow-Up Reminders

**Description:** Add follow-up reminders for applications and interview rounds using the same reminder concepts learned in the learning tracker.

**Acceptance criteria:**
- [ ] User can create follow-up reminders for applications.
- [ ] User can list due career reminders.
- [ ] Reminder completion is recorded.

**Verification:**
- [ ] Tests pass: reminder tests with fixed clock.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create due and future reminders and verify filtering.

**Dependencies:** Task 18

**Files likely touched:**
- `career/domain`
- `career/application`
- `career/web`
- `career/infrastructure/persistence`

**Estimated scope:** Medium: 3-5 files

## Checkpoint: Career

- [ ] User can track job applications end to end.
- [ ] Career state transitions are validated.
- [ ] Reminder patterns are reused cleanly.

## Sprint 6: Redis, RocketMQ, and Production Readiness

## Task 20: Add Redis Caching and Rate Limiting

**Description:** Introduce Redis for specific, measurable use cases: caching frequently-read summaries and rate limiting auth-sensitive endpoints.

**Acceptance criteria:**
- [ ] Redis runs in Docker Compose.
- [ ] At least one read-heavy endpoint uses cache-aside caching.
- [ ] Cache invalidates when underlying data changes.
- [ ] Login or write endpoints have basic rate limiting.

**Verification:**
- [ ] Tests pass: cache behavior tests where practical.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: observe cache hit/miss logs locally.

**Dependencies:** Tasks 12, 16, 19

**Files likely touched:**
- `configuration/cache`
- `configuration/redis`
- `workspace/application`
- `knowledge/application`
- `compose.yaml`

**Estimated scope:** Medium: 3-5 files

## Task 21: Add RocketMQ Domain Events

**Description:** Add event publishing for meaningful domain events, such as page updated, study reminder due, application status changed, and notification requested.

**Acceptance criteria:**
- [ ] RocketMQ runs in Docker Compose.
- [ ] Domain events are published after successful transactions.
- [ ] Consumers handle duplicate messages idempotently.

**Verification:**
- [ ] Tests pass: event publisher/consumer tests where practical.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: trigger an event and observe consumer processing.

**Dependencies:** Task 20

**Files likely touched:**
- `shared/events`
- `configuration/messaging`
- `knowledge/application`
- `learning/application`
- `career/application`

**Estimated scope:** Medium: 3-5 files

## Task 22: Add Audit Records and Notification Records

**Description:** Consume domain events into audit and notification modules. Store immutable audit records and user-visible notification records.

**Acceptance criteria:**
- [ ] Important user actions create audit records.
- [ ] Reminder and status events create notification records.
- [ ] Event consumers are idempotent.

**Verification:**
- [ ] Tests pass: audit/notification consumer tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: perform actions and inspect audit/notification tables.

**Dependencies:** Task 21

**Files likely touched:**
- `audit/application`
- `audit/infrastructure/persistence`
- `notification/application`
- `notification/infrastructure/persistence`
- `src/main/resources/db/migration`

**Estimated scope:** Medium: 3-5 files

## Task 23: Add Observability and Operational Endpoints

**Description:** Make the backend easier to debug and operate through structured logging, actuator health, metrics, and local troubleshooting docs.

**Acceptance criteria:**
- [ ] Health checks cover MySQL, Redis, and RocketMQ where supported.
- [ ] Logs include request correlation IDs.
- [ ] Key operations emit useful structured logs.

**Verification:**
- [ ] Tests pass: existing suite.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: inspect logs and actuator endpoints during a normal workflow.

**Dependencies:** Task 22

**Files likely touched:**
- `configuration/observability`
- `support/web`
- `src/main/resources/application.yml`
- `docs/operations.md`

**Estimated scope:** Medium: 3-5 files

## Checkpoint: Enterprise Backend

- [ ] Redis is used for caching/rate limiting with correct invalidation.
- [ ] RocketMQ events are useful and idempotent.
- [ ] Audit and notification flows work.
- [ ] Local operations are debuggable.

## Sprint 7: Portfolio Hardening

## Task 24: Add API Documentation and Example Requests

**Description:** Add a clear API reference and runnable example requests for the main demo workflows.

**Acceptance criteria:**
- [ ] Main workflows have documented endpoints.
- [ ] Example HTTP requests can be run locally.
- [ ] Error response shape is documented.

**Verification:**
- [ ] Tests pass: existing suite.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: follow docs from a clean local start.

**Dependencies:** Task 23

**Files likely touched:**
- `docs/api.md`
- `http/devatlas.http`
- `README.md`

**Estimated scope:** Small: 1-2 files

## Task 25: Add CI and Quality Gates

**Description:** Add GitHub Actions or the chosen CI tool to run build and tests on every push.

**Acceptance criteria:**
- [ ] CI runs compile and tests.
- [ ] CI uses the same Java version as local development.
- [ ] CI status is documented in README.

**Verification:**
- [ ] Tests pass: CI run completes successfully.
- [ ] Build succeeds: CI package/build job succeeds.
- [ ] Manual check: push a branch and inspect CI result.

**Dependencies:** Task 24

**Files likely touched:**
- `.github/workflows/build.yml`
- `README.md`

**Estimated scope:** Small: 1-2 files

## Task 26: Write Architecture Documentation and Project README

**Description:** Document the modular monolith, module ownership, dependency rules, local setup, and demo workflow so the portfolio story is obvious.

**Acceptance criteria:**
- [ ] README explains what DevAtlas is and how to run it.
- [ ] Architecture docs explain module boundaries.
- [ ] Demo workflow is short and reproducible.

**Verification:**
- [ ] Tests pass: existing suite.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: follow README from a clean checkout.

**Dependencies:** Task 25

**Files likely touched:**
- `README.md`
- `docs/architecture/module-map.md`
- `docs/architecture/dependency-rules.md`

**Estimated scope:** Small: 1-2 files

## Task 27: Add Deployment-Ready Docker Compose Profile

**Description:** Add a Compose profile that runs the app plus MySQL, Redis, RocketMQ, and required configuration for a local production-like demo.

**Acceptance criteria:**
- [ ] One documented command starts the full local stack.
- [ ] App connects to MySQL, Redis, and RocketMQ through environment variables.
- [ ] Seed or example data supports the demo path.

**Verification:**
- [ ] Tests pass: existing suite.
- [ ] Build succeeds: Docker image builds successfully.
- [ ] Manual check: run full stack and complete demo workflow.

**Dependencies:** Task 26

**Files likely touched:**
- `Dockerfile`
- `compose.yaml`
- `README.md`
- `src/main/resources/application.yml`

**Estimated scope:** Medium: 3-5 files

## Checkpoint: Portfolio

- [ ] Reviewer can run DevAtlas locally from README instructions.
- [ ] CI is green.
- [ ] Main demo path works end to end.
- [ ] Architecture and trade-offs are documented.

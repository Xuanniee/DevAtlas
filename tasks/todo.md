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
- [x] `POST /api/pages` creates a child page under the given `parentId` (root or non-root). Fixed and verified live (2026-08-30): `@RequestBody` added to `createPage`, `PageRepository.insert`/`update` return `void`. Real JSON `POST /api/pages` → `200`, correct `workspaceId` derived from parent, correct `parentId`.
- [x] A request with no `parentId` is rejected with a validation error (root pages are never created here). Verified live (2026-08-30): `400`, `"parentId cannot be NULL since root pages are created at workspace level"`.
- [x] `workspaceId` on the new page is derived from the parent page's `workspaceId`, never taken from client input. Verified live (2026-08-30).
- [x] A `parentId` for a page that doesn't exist (or is archived) is rejected. Verified live (2026-08-30): nonexistent `parentId` → `404 Page with pageId 99999 is not found`. Archived case covered by the same `findById` filter (now `archived = FALSE`, previously `archived_at IS NULL` — filtering column changed when the module moved to a dedicated `archived` boolean, same exclusion behavior either way).
- [x] New pages default their name to "Untitled" when no name is supplied.

**Verification:**
- [ ] Tests pass: page service and mapper integration tests. Still no automated tests exist for the `page` module — everything verified here was via live HTTP checks, not `mvn test`.
- [x] Build succeeds: `./mvnw package` or `mvn package`
- [x] Manual check: create a child page under a workspace's root page, and a grandchild under that, through HTTP. Done (2026-08-30).

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

Archived pages follow Confluence's model, not a plain soft-delete: "archived" means pulled out of normal navigation and search, not made inaccessible. Archived content stays fully readable through its own dedicated view — that's the whole point of archiving over deleting (see Task 9 for why). `PageRepository` already has `findByArchivedId`/`getAllArchivedPages` scaffolded for this; they need their own read endpoints alongside the normal ones.

**Acceptance criteria:**
- [x] `GET /api/pages/{pageId}` returns page details, whether the page is a root page or a child page. Verified live (2026-08-30) against real MySQL.
- [x] `GET /api/pages/{pageId}/children` returns direct children. Fixed and verified live (2026-08-30): `PageController.getAllPages` now uses `@PathVariable Long pageId` matching the URL template, calling `pageService.findAll(pageId)`. Returns the correct child list.
- [x] Archived pages are excluded from normal child listings. Verified live (2026-08-30): after archiving a page, `GET .../children` on its former parent returns `[]`, and `GET /api/pages/{archivedId}` directly returns `404`.
- [x] Page responses (create, get, list) include the page's own `id`. Fixed and verified live (2026-08-30): `PageResponse` constructor and `PageMapper.toResponse` both wired correctly — every response now includes a real `id`.
- [x] `GET /api/pages/{pageId}/archived` (or equivalent) returns an archived page's details, and a corresponding endpoint lists archived children — a dedicated view, not just "excluded elsewhere." Fully done, verified live (2026-08-30): `GET /api/pages/archive`, `GET /api/pages/archive/{pageId}`, and `GET /api/pages/archive/{pageId}/children` all work correctly — `findAllArchivedPages`/`findAllArchived` naming now matches, and the archive-root listing correctly shows only the top of an archived subtree (e.g. archiving Child+Grandchild together shows only Child in the roots list).

**Verification:**
- [ ] Tests pass: focused API and mapper tests. Still no automated tests for the `page` module.
- [x] Build succeeds: `./mvnw package` or `mvn package`
- [x] Manual check: create a small page tree and browse it through HTTP. Done (2026-08-30) — create, get, list-children, archived-exclusion, and the full archived-view endpoint set all verified live.

**Dependencies:** Task 7

**Files likely touched:**
- `knowledge/web`
- `knowledge/application`
- `knowledge/infrastructure/persistence`
- `src/main/resources/mybatis/knowledge`

**Estimated scope:** Medium: 3-5 files

## Task 9: Rename, Move, and Archive Pages

**Description:** Add lifecycle operations for changing page title, moving a page within the hierarchy, and archiving a page. The move operation should prevent cycles.

Archiving is not deletion — it's a reversible, "settled but not gone" state (the Confluence model): pulled out of normal navigation/search, but still fully intact and viewable through its own dedicated read path (Task 8), and restorable later. That's why it's a `POST` action on the resource, not a `DELETE` of it — nothing is actually being removed, so `DELETE`'s semantics (destroy the resource) don't fit. Archiving a page cascades to its entire subtree, so children are never left dangling under an invisible parent; restoring should be considered the same way (restoring a child whose ancestor is still archived needs a decision — restore top-down only, or pull the ancestor chain back too).

**Acceptance criteria:**
- [x] `PATCH /api/pages/{pageId}/title` renames a page. Verified live (2026-08-30).
- [x] `POST /api/pages/{pageId}/move` moves a page to another parent or root, **and prevents cycles**. Fixed and verified live (2026-08-30): `MoveUtils.wouldCreatePageMoveCycles` walks the full ancestor chain from the new parent (checking the new parent itself first, then stepping up via `parentId` until it hits `null`), throwing `CyclicPageMoveException` (409) if it ever reaches the page being moved. Confirmed live: moving under a root page now works with no NPE; a 3-level cycle attempt and a self-parent attempt are both correctly rejected with `409`; a normal valid move still succeeds.
- [x] `POST /api/pages/{pageId}/archive` archives a page without hard deletion (changed from `DELETE /api/pages/{pageId}`). Verified live.
- [x] Archiving a page cascades: all of its descendants are archived too, not just the page itself. Verified live across a 3-level tree (root → child → grandchild), all three correctly archived with `archived_at` timestamps.
- [x] `POST /api/pages/{pageId}/restore` (or equivalent) reverses an archive. Fixed and verified live (2026-08-30): `currPage.setArchivedAt(null)` added right after `resetArchivedDatetime`, so the in-memory object and the database now agree. Confirmed: response shows `"archivedAt":null` and the raw DB row shows `archived_at = NULL` for the same request.

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
- [x] Page content can be updated. Fixed and verified live (2026-09-01): content at creation works, and a content-only `PATCH /api/pages/{pageId}` (no `name` required) correctly updates just the content, confirmed against the raw database.
- [x] Updating content creates a page revision. Fixed and verified live (2026-09-01): two sequential content updates produced two correctly-numbered revisions (1, then 2) with the right content and update notes, confirmed in the `revisions` table directly.
- [x] Revision metadata can be listed for a page. Verified live (2026-09-01): `GET /api/revisions/v1/{pageId}/findAll` returns the full, correctly-ordered revision history.

**Verification:**
- [ ] Tests pass: revision service and mapper tests. No automated tests exist for the revision module — everything verified here was via live HTTP + direct DB checks.
- [x] Build succeeds: `./mvnw package` or `mvn package`
- [x] Manual check: update a page twice and verify two revisions exist. Done (2026-09-01) — confirmed live against real MySQL.

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
- [x] Users can register with email and password. Verified via code review (2026-09-06): `AuthController.register()` → `AuthServiceImpl.register()` rejects duplicate emails (`EmailAlreadyExistsException`) and inserts a new `User`.
- [x] Users can log in and receive the chosen auth mechanism. Verified via code review (2026-09-06): `AuthServiceImpl.login()` validates the password and issues a JWT via `JwtServiceImpl.issueToken`.
- [x] Passwords are hashed, never stored in plain text. Verified via code review (2026-09-06): `SecurityConfig.passwordEncoder()` uses `Argon2PasswordEncoder` via `DelegatingPasswordEncoder`; only `passwordHash` is ever persisted.

**Verification:**
- [ ] Tests pass: identity service and security tests. Still no automated tests exist for the `user` module. Also, the full suite currently can't run at all: `mvn clean test` fails during Flyway migration on `V5__create__workspace_members.sql` (empty column list, `CREATE TABLE workspace_members()` — a Task 13 scaffold, unrelated to Task 11) before any test executes.
- [ ] Build succeeds: `./mvnw package` or `mvn package`. `mvn compile` succeeds, but `mvn package`/`mvn test` currently fails due to the broken `V5` migration above, not because of Task 11 code.
- [ ] Manual check: register, log in, and call an authenticated endpoint. Not yet performed live over HTTP — only verified via static code review so far.
- [ ] Known gap (not blocking, but worth fixing before sign-off): `InvalidCredentialsException` extends `ApplicationException` directly, but `GlobalExceptionHandler` only has handlers for `NotFoundException`/`ConflictException`/catch-all — a wrong-password login currently returns a 500 instead of a proper 4xx.

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
- [x] Protected APIs require authentication. Verified via code review (2026-09-06): `SecurityConfig` requires authentication on every route except `/api/auth/**` and `/actuator/health`; `JwtAuthenticationFilter` populates `SecurityContextHolder` from a valid Bearer token.
- [x] Users can access their own workspaces/pages. Verified via code review (2026-09-06): every controller resolves the caller via `@AuthenticationPrincipal Long ownerId`, threaded through the command/query builders into `WorkspaceServiceImpl`/`PageServiceImpl` and every repository call — the old hardcoded `ownerId = 1L` placeholder is gone.
- [x] Users cannot access another user's private workspace/pages. Verified via code review (2026-09-06): audited every statement in `WorkspaceMapper.xml`, `PageMapper.xml`, and `PageRevisionMapper.xml` — all reads, writes, and deletes now scope by `owner_id` in the `WHERE` clause. (An earlier gap found and fixed during this review: `PageMapper.xml`'s `findAll`, `update`'s `WHERE`, `findArchivedById`, `findAllArchivedChildren`, `delete`, `resetArchivedDatetime`, and `isExistingSiblingPageByParentPage` were missing the `owner_id` filter — all now corrected.)

**Verification:**
- [ ] Tests pass: authorization tests for workspace and page APIs. None exist yet (e.g. confirming user B gets a 404 fetching user A's workspace/page). Also currently blocked suite-wide by the broken `V5__create__workspace_members.sql` migration (see Task 11) — unrelated to Task 12's own code.
- [ ] Build succeeds: `./mvnw package` or `mvn package`. `mvn compile` succeeds; `mvn package`/`mvn test` currently fails due to the `V5` migration issue, not Task 12 code.
- [ ] Manual check: create two users and verify isolation. Not yet performed live over HTTP — only verified via static code review of the SQL/service/controller layers so far.

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
- [x] Workspace owner is automatically a member. Confirmed (2026-09-13): `WorkspaceServiceImpl.createWorkspace` inserts a `WorkspaceMember` row with role `OWNER` in the same `@Transactional` method that creates the workspace and root page.
- [x] Membership roles support at least owner and member. Confirmed (2026-09-13): `WorkspaceMemberRole` enum has `VIEWER`, `EDITOR`, `ADMIN`, `OWNER` with an `accessLevel` int and `isAtLeast`/`isAtMost`/`isLessThan` comparison helpers.
- [x] Role checks protect write operations. Confirmed (2026-09-13): `MembershipRoleUtils.validateOperationByRole` is now called from `PageServiceImpl` (create/read/move/update/archive/find-archived) and `WorkspaceServiceImpl`/`WorkspaceMemberServiceImpl`, each requiring a minimum role per operation. **Caveat found while checking:** `GlobalExceptionHandler` has two `@ExceptionHandler(Exception.class)` methods (`handleUnexpected` and `handleForbidden`) mapped to the same exception type on the same class — an ambiguous mapping, so `UnauthorisedWorkspaceMemberException` (which extends the generic `Exception` bucket via `ForbiddenException` → `ApplicationException`) isn't guaranteed to route to the intended 401/403 handler. Not fixed — flagging per the "don't silently fix" rule.

**Verification:**
- [ ] Tests pass: membership service and authorization tests. Still no tests exist anywhere for `workspace_members` (checked 2026-09-13) — nothing to run.
- [ ] Build succeeds: `./mvnw package` or `mvn package`. Currently **fails** (2026-09-13): `mvn clean compile` errors on `PageMapper.java:20` — `CreatePageCommand` no longer has an `ownerId()` method, from an in-progress `ownerId` → `userId` rename across controllers/commands that hasn't been finished everywhere yet. This is in-progress work, not touched.
- [ ] Manual check: verify owner-only and member-allowed operations. Not done — can't run the app to test this while the build is broken.

**Dependencies:** Task 12

**Files likely touched:**
- `workspace/domain`
- `workspace/application`
- `workspace/infrastructure/persistence`
- `src/main/resources/db/migration`

**Estimated scope:** Medium: 3-5 files

## Task 14: Add Email Verification via One-Time Code

**Status:** Deferred (2026-09-13) — moved to the backlog in `tasks/plan.md`. Not core to a Confluence-clone portfolio scope; revisit after Sprint 6 (Portfolio Hardening) if there's time.

**Description:** After registration, require the user to verify their email address using a short-lived, single-use code before the account is treated as fully active. Unverified accounts should still be able to log in (so the user isn't locked out), but verification status should be checkable by other parts of the app for later gating decisions.

**Acceptance criteria:**
- [ ] Registering a user generates a one-time verification code and (at minimum) logs/returns it for local development, without a real email provider wired up yet.
- [ ] The code is short-lived (expires after a fixed window) and single-use (can't be replayed after a successful verification).
- [ ] A verification endpoint marks the user's email as verified when given a valid, unexpired code for that user.
- [ ] An expired or already-used code is rejected with a clear error, not a generic 500.
- [ ] A user can request a new code if theirs expired (rate-limit or cooldown this later if it becomes a real concern; not required for this task).

**Verification:**
- [ ] Tests pass: verification service tests covering valid, expired, and already-used code cases.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: register a user, verify with the generated code, confirm `email_verified` flips to true; confirm reusing the same code afterward fails.

**Dependencies:** Task 11

**Files likely touched:**
- `user/domain`
- `user/application`
- `user/adapter`
- `src/main/resources/db/migration` (new column/table for verification codes, or a column on `users` for verification status)

**Estimated scope:** Medium: 3-5 files

## Task 15: Add Forgot Password / Password Reset Flow

**Status:** Deferred (2026-09-13) — moved to the backlog in `tasks/plan.md`, alongside Task 14. Not core to a Confluence-clone portfolio scope.

**Description:** Let a user who forgot their password request a reset without being logged in, using a short-lived one-time token/code, then set a new password. Reuses the same "generate a short-lived single-use secret, validate it, consume it" shape as Task 14's email verification — worth building the second one deliberately similarly to the first, rather than as an unrelated one-off.

**Acceptance criteria:**
- [ ] A user can request a password reset by email, which generates a short-lived, single-use reset token (at minimum logged/returned for local dev, no real email provider yet).
- [ ] The reset endpoint accepts the token and a new password, hashes it the same way registration does, and invalidates the token afterward.
- [ ] An expired or already-used token is rejected with a clear error, not a generic 500.
- [ ] Requesting a reset for an email that doesn't exist doesn't reveal whether that email is registered (avoid leaking account existence — same reasoning as the 404-vs-403 ownership decision from Task 12).
- [ ] After a successful reset, existing JWTs issued before the reset should ideally no longer be treated as trustworthy forever (this may be a stretch goal depending on whether token invalidation/blacklisting exists yet — note as a known limitation if skipped).

**Verification:**
- [ ] Tests pass: reset-request and reset-confirmation service tests covering valid, expired, already-used, and unknown-email cases.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: request a reset, use the generated token to set a new password, log in with the new password, confirm the old password no longer works.

**Dependencies:** Task 11, Task 14 (shares the short-lived-code pattern; build after email verification so the pattern is established once, not reinvented)

**Files likely touched:**
- `user/domain`
- `user/application`
- `user/adapter`
- `src/main/resources/db/migration` (new table/column for reset tokens)

**Estimated scope:** Medium: 3-5 files

## Checkpoint: Security

- [ ] Authentication works.
- [ ] Ownership and membership checks are enforced.
- [ ] Security behavior has automated tests.

## Sprint 4: Real-Time Collaborative Editing

**Reprioritized in (2026-09-13, see `tasks/plan.md`):** this sprint now runs immediately after Sprint 3, ahead of Learning/Career Tracker — it's the feature that actually differentiates a Confluence clone from basic CRUD, so it's no longer stuck behind lower-value boilerplate.

## Task 30: Add WebSocket Infrastructure for Live Page Sessions

**Description:** Add STOMP-over-WebSocket support so a client can open a live session scoped to a specific page, joining a per-page "room" that the server tracks. This is the transport layer collaborative editing and presence build on top of.

**Acceptance criteria:**
- [ ] A client can open a WebSocket connection authenticated with the same JWT used for REST calls (reuse `JwtService`, don't invent a second auth mechanism).
- [ ] A client can join a specific page's session (e.g. subscribing to `/topic/pages/{pageId}`) only if their workspace membership role allows at least `VIEWER` access to that page (reuse `MembershipRoleUtils`, don't duplicate the check).
- [ ] Joining a page session records the connecting user against that page's session state.
- [ ] Disconnecting (or an explicit leave) removes that user from the session state.

**Verification:**
- [ ] Tests pass: WebSocket handshake and join/leave session-state tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: open two authenticated WebSocket clients on the same `pageId` and confirm the server-side session state shows both.

**Dependencies:** Task 13

**Files likely touched:**
- new `realtime` module: `adapter` (WebSocket/STOMP config, JWT handshake interceptor), `application` (session tracking), `domain`
- `pom.xml` (`spring-boot-starter-websocket`)

**Estimated scope:** Medium: 3-5 files

## Task 31: Add Redis Pub/Sub for Edit Broadcast and Presence

**Description:** Back the session/presence state from Task 30 with Redis pub/sub so an edit or presence change on one app instance reaches every subscribed client, not just the ones held in one JVM's in-memory map. This is the first real (non-toy) use of Redis in the project, per the reprioritization rationale in `tasks/plan.md`.

**Acceptance criteria:**
- [ ] A page-content change from one client is broadcast, via Redis pub/sub, to every other client subscribed to that page's session — including a client connected to a different app instance in a multi-instance run.
- [ ] Presence join/leave events are broadcast the same way.
- [ ] Redis-tracked presence is rebuilt correctly after an app restart (no permanently "stuck" phantom participants).

**Verification:**
- [ ] Tests pass: broadcast/presence integration tests using Testcontainers Redis.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: two browser tabs/WebSocket clients on the same page, running against two local app instances if feasible, confirm edits and presence propagate both ways.

**Dependencies:** Task 30

**Files likely touched:**
- `realtime/infrastructure` (Redis pub/sub listener/publisher)
- `pom.xml` (`spring-boot-starter-data-redis`)
- `compose.yaml` (add a `redis` service)

**Estimated scope:** Medium: 3-5 files

## Task 32: Add Concurrent Edit Conflict Handling for Page Content

**Description:** Handle two users saving edits to the same page at nearly the same time. Start with optimistic concurrency — a `version` column on pages, incremented on every content update — as a deliberate, honest stepping stone toward full operational-transform/CRDT merging later, not a final answer. A save against a stale version is rejected, not silently overwritten.

**Acceptance criteria:**
- [ ] `pages` rows carry a `version` column that increments on every content update.
- [ ] Updating a page with an outdated `version` is rejected with a clear conflict response (not a generic 500), which includes the current server version/content so the client can reconcile.
- [ ] Two sequential updates using the correct version each succeed and the version increments each time.

**Verification:**
- [ ] Tests pass: unit and integration tests covering the matching-version and stale-version cases.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: simulate two near-simultaneous `PATCH /api/pages/{pageId}` calls with the same starting version via HTTP; confirm the second one gets a conflict response, not a silent overwrite.

**Dependencies:** Task 10 (page revision history — version tracking is a natural extension of the same table)

**Files likely touched:**
- `page/domain/entity/Page.java`
- `src/main/resources/mapper/PageMapper.xml`
- `src/main/resources/db/migration` (new `version` column)
- `page/application/impl/PageServiceImpl.java`

**Estimated scope:** Medium: 3-5 files

## Task 33: Add Live Presence Indicators

**Description:** Surface who is currently viewing/editing a page in real time, built on the Redis-backed presence data from Task 31, exposed both over the WebSocket topic (for live updates) and a REST fallback endpoint (for a client that just loaded the page).

**Acceptance criteria:**
- [ ] `GET /api/pages/{pageId}/presence` returns the current list of users present on a page.
- [ ] The WebSocket topic for a page pushes a presence-changed event whenever someone joins or leaves.
- [ ] A user's presence expires automatically if their connection drops without a clean leave (e.g. a TTL-based heartbeat in Redis), so a crashed client doesn't show as "present" forever.

**Verification:**
- [ ] Tests pass: presence expiry/heartbeat tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: two clients join, one disconnects abruptly (kill the tab/process without a clean leave), confirm it drops off presence after the TTL window.

**Dependencies:** Task 31

**Files likely touched:**
- `realtime` module (presence endpoint, heartbeat/TTL logic)
- `page/adapter/PageController.java` (or a dedicated presence controller)

**Estimated scope:** Small-Medium: 2-4 files

## Checkpoint: Collaboration

- [ ] Two authenticated clients can join the same page's live session and see each other present.
- [ ] A content edit from one client reaches every other connected client, including across app instances.
- [ ] Conflicting near-simultaneous saves are detected and surfaced, not silently lost.
- [ ] Presence recovers correctly after a dropped connection or app restart.

## Sprint 4 (Deferred): Learning Tracker

**Status:** Deferred (2026-09-13) — moved to the backlog in `tasks/plan.md`, behind the new Sprint 4 (Real-Time Collaborative Editing) above. Not core to a Confluence-clone portfolio scope.

## Task 16: Track Skills and Learning Goals

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

## Task 17: Track Learning Resources and Study Sessions

**Description:** Allow users to attach resources to goals and log study sessions with duration, notes, and confidence rating.

**Acceptance criteria:**
- [ ] User can add resources to a learning goal.
- [ ] User can log study sessions.
- [ ] Study sessions update goal progress summaries.

**Verification:**
- [ ] Tests pass: progress calculation and mapper tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: add resources, log sessions, and inspect progress.

**Dependencies:** Task 16

**Files likely touched:**
- `learning/domain`
- `learning/application`
- `learning/web`
- `learning/infrastructure/persistence`

**Estimated scope:** Medium: 3-5 files

## Task 18: Generate Review Reminders

**Description:** Add deterministic reminder generation for spaced review. Start with a scheduled Spring job and database records before adding RocketMQ.

**Acceptance criteria:**
- [ ] Completed study sessions can generate future review reminders.
- [ ] Reminder generation is idempotent.
- [ ] User can list due reminders.

**Verification:**
- [ ] Tests pass: reminder scheduling tests with fixed clock.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create a session and verify due reminders.

**Dependencies:** Task 17

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

## Sprint 5 (Deferred): Career Tracker

**Status:** Deferred (2026-09-13) — moved to the backlog in `tasks/plan.md`. Not core to a Confluence-clone portfolio scope.

## Task 19: Track Companies and Job Applications

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

## Task 20: Track Interview Rounds and Application Status Changes

**Description:** Add interview rounds and explicit status history to make the career tracker more than CRUD.

**Acceptance criteria:**
- [ ] User can add interview rounds to an application.
- [ ] Application status changes are recorded in history.
- [ ] Invalid status transitions are rejected.

**Verification:**
- [ ] Tests pass: status transition tests and mapper tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: move an application through multiple statuses.

**Dependencies:** Task 19

**Files likely touched:**
- `career/domain`
- `career/application`
- `career/web`
- `career/infrastructure/persistence`

**Estimated scope:** Medium: 3-5 files

## Task 21: Add Follow-Up Reminders

**Description:** Add follow-up reminders for applications and interview rounds using the same reminder concepts learned in the learning tracker.

**Acceptance criteria:**
- [ ] User can create follow-up reminders for applications.
- [ ] User can list due career reminders.
- [ ] Reminder completion is recorded.

**Verification:**
- [ ] Tests pass: reminder tests with fixed clock.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: create due and future reminders and verify filtering.

**Dependencies:** Task 20

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

## Sprint 5 (revised order): Redis, RocketMQ, and Production Readiness

**Reprioritized (2026-09-13, see `tasks/plan.md`):** this sprint now runs directly after Sprint 4 (Real-Time Collaborative Editing), not after the deferred Learning/Career Tracker sprints. Redis pub/sub already exists from Task 31 — Task 22 below adds Redis for general caching/rate-limiting instead, and Task 23 (RocketMQ) is now grounded in a real source of events: page edit/revision activity from Sprint 4.

## Task 22: Add Redis Caching and Rate Limiting

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

## Task 23: Add RocketMQ Domain Events

**Description:** Add event publishing for meaningful domain events, such as page updated, study reminder due, application status changed, and notification requested.

**Acceptance criteria:**
- [ ] RocketMQ runs in Docker Compose.
- [ ] Domain events are published after successful transactions.
- [ ] Consumers handle duplicate messages idempotently.

**Verification:**
- [ ] Tests pass: event publisher/consumer tests where practical.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: trigger an event and observe consumer processing.

**Dependencies:** Task 22

**Files likely touched:**
- `shared/events`
- `configuration/messaging`
- `knowledge/application`
- `learning/application`
- `career/application`

**Estimated scope:** Medium: 3-5 files

## Task 24: Add Audit Records and Notification Records

**Description:** Consume domain events into audit and notification modules. Store immutable audit records and user-visible notification records.

**Acceptance criteria:**
- [ ] Important user actions create audit records.
- [ ] Reminder and status events create notification records.
- [ ] Event consumers are idempotent.

**Verification:**
- [ ] Tests pass: audit/notification consumer tests.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: perform actions and inspect audit/notification tables.

**Dependencies:** Task 23

**Files likely touched:**
- `audit/application`
- `audit/infrastructure/persistence`
- `notification/application`
- `notification/infrastructure/persistence`
- `src/main/resources/db/migration`

**Estimated scope:** Medium: 3-5 files

## Task 25: Add Observability and Operational Endpoints

**Description:** Make the backend easier to debug and operate through structured logging, actuator health, metrics, and local troubleshooting docs.

**Acceptance criteria:**
- [ ] Health checks cover MySQL, Redis, and RocketMQ where supported.
- [ ] Logs include request correlation IDs.
- [ ] Key operations emit useful structured logs.

**Verification:**
- [ ] Tests pass: existing suite.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: inspect logs and actuator endpoints during a normal workflow.

**Dependencies:** Task 24

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

## Sprint 6 (revised order): Portfolio Hardening

## Task 26: Add API Documentation and Example Requests

**Description:** Add a clear API reference and runnable example requests for the main demo workflows.

**Acceptance criteria:**
- [ ] Main workflows have documented endpoints.
- [ ] Example HTTP requests can be run locally.
- [ ] Error response shape is documented.

**Verification:**
- [ ] Tests pass: existing suite.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: follow docs from a clean local start.

**Dependencies:** Task 25

**Files likely touched:**
- `docs/api.md`
- `http/devatlas.http`
- `README.md`

**Estimated scope:** Small: 1-2 files

## Task 27: Add CI and Quality Gates

**Description:** Add GitHub Actions or the chosen CI tool to run build and tests on every push.

**Acceptance criteria:**
- [ ] CI runs compile and tests.
- [ ] CI uses the same Java version as local development.
- [ ] CI status is documented in README.

**Verification:**
- [ ] Tests pass: CI run completes successfully.
- [ ] Build succeeds: CI package/build job succeeds.
- [ ] Manual check: push a branch and inspect CI result.

**Dependencies:** Task 26

**Files likely touched:**
- `.github/workflows/build.yml`
- `README.md`

**Estimated scope:** Small: 1-2 files

## Task 28: Write Architecture Documentation and Project README

**Description:** Document the modular monolith, module ownership, dependency rules, local setup, and demo workflow so the portfolio story is obvious.

**Acceptance criteria:**
- [ ] README explains what DevAtlas is and how to run it.
- [ ] Architecture docs explain module boundaries.
- [ ] Demo workflow is short and reproducible.

**Verification:**
- [ ] Tests pass: existing suite.
- [ ] Build succeeds: `./mvnw package` or `mvn package`
- [ ] Manual check: follow README from a clean checkout.

**Dependencies:** Task 27

**Files likely touched:**
- `README.md`
- `docs/architecture/module-map.md`
- `docs/architecture/dependency-rules.md`

**Estimated scope:** Small: 1-2 files

## Task 29: Add Deployment-Ready Docker Compose Profile

**Description:** Add a Compose profile that runs the app plus MySQL, Redis, RocketMQ, and required configuration for a local production-like demo.

**Acceptance criteria:**
- [ ] One documented command starts the full local stack.
- [ ] App connects to MySQL, Redis, and RocketMQ through environment variables.
- [ ] Seed or example data supports the demo path.

**Verification:**
- [ ] Tests pass: existing suite.
- [ ] Build succeeds: Docker image builds successfully.
- [ ] Manual check: run full stack and complete demo workflow.

**Dependencies:** Task 28

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

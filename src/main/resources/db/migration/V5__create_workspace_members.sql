/**
  A Workspace can have many members with different roles.
  A user can have a role in many workspaces.
  Thus, Many to Many relationship.

  However, cannot use existing user/workspace table, need a JOIN Table
  because Alice can be owner of Workspace A, and member of Workspace B,
  and then the constraints wont hold
 */

CREATE TABLE workspace_members(
    id           BIGINT          AUTO_INCREMENT NOT NULL,
    workspace_id BIGINT          NOT NULL,
    user_id      BIGINT          NOT NULL,
    role         VARCHAR(50)     NOT NULL,
    joined_at    DATETIME(3)     NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3),

    CONSTRAINT pk_workspace_members PRIMARY KEY (id),
    CONSTRAINT uq_workspace_members                         -- A user can only have one role in the same workspace
        UNIQUE (workspace_id, user_id),
    CONSTRAINT fk_workspace_members_workspaces
        FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    CONSTRAINT fk_workspace_members_users
        FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE = InnoDB;
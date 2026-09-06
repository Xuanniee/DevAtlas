-- Workspaces must exist first since pages have a FK reference to workspaces
CREATE TABLE pages (
    id           BIGINT          AUTO_INCREMENT NOT NULL,
    workspace_id BIGINT          NOT NULL,
    parent_id    BIGINT,                                             -- Nullable as first page has no parent
    name         VARCHAR(256)    NOT NULL,
    content      LONGTEXT                ,
    owner_id     BIGINT          NOT NULL,
    archived     BOOLEAN         NOT NULL DEFAULT  FALSE,           -- Pages are never archived by default
    created_at   DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    modified_at  DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    archived_at  DATETIME(3),

    CONSTRAINT pk_pages PRIMARY KEY (id),
    CONSTRAINT fk_pages_workspace
       FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    CONSTRAINT fk_pages_parent
       FOREIGN KEY (parent_id) REFERENCES pages(id),
    CONSTRAINT fk_pages_owner
       FOREIGN KEY (owner_id) REFERENCES users(id)
) ENGINE = InnoDB;

-- Need to alter workspaces separately since they have FK to pages which initialised later
ALTER TABLE workspaces
    ADD CONSTRAINT fk_workspaces_root_page
        FOREIGN KEY (root_page_id)
            REFERENCES pages(id);

-- Create Indexes on common read queries
CREATE INDEX idx_pages_workspace_parent ON pages (workspace_id, parent_id);
CREATE INDEX idx_pages_owner_id ON pages (owner_id);

-- Development seed data
INSERT INTO users (name, email, password_hash)
VALUES ('Test User', 'test@example.com', '4203e02f448520c04e3f15dfeeda0859');
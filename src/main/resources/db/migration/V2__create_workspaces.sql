CREATE TABLE workspaces (
    id              BIGINT AUTO_INCREMENT NOT NULL,
    name            VARCHAR(256) NOT NULL,
    slug            VARCHAR(256) NOT NULL,                                  -- Unique slug for URL
    description     VARCHAR(512),
    status          VARCHAR(20) NOT NULL,                                   -- Indicates whether workspace is still used
    visibility      VARCHAR(20) NOT NULL,                                   -- Determines who can see this workspace
    category        VARCHAR(20) NOT NULL,                                   -- Determines the workspace type
    permissions     VARCHAR(20) NOT NULL,                                   -- Determine the roles this workspace support
    root_page_id    BIGINT,                                                 -- The first page this workspace sees
    owner_id        BIGINT NOT NULL,
    created_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    modified_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    archived_at     DATETIME(3),

    CONSTRAINT pk_workspaces PRIMARY KEY (id),
    CONSTRAINT uq_workspaces_slug UNIQUE (slug),                            -- Ensure Slug is unique
    CONSTRAINT fk_workspaces_owner FOREIGN KEY (owner_id)                   -- Ensures FK reference
        REFERENCES users(id)
) ENGINE = InnoDB;

CREATE INDEX idx_workspaces_owner_id ON workspaces (owner_id);
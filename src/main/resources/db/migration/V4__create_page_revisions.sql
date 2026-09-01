CREATE TABLE revisions (
    id                  BIGINT          AUTO_INCREMENT NOT NULL,
    page_id             BIGINT          NOT NULL,                               -- FK Reference to the revised page
    revision_number     INT             NOT NULL,                               -- Order of revision for this page
    name                VARCHAR(256)    NOT NULL,                               -- Revision Name
    content             LONGTEXT,                                               -- Snapshot of Page Body
    note                VARCHAR(500),                                           -- Short summary of what the revision is
    edited_by           BIGINT          NOT NULL,                               -- User who revised the page,
                                                                                -- may not be owner
    created_at          DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),  -- No modified as revisions are immutable

    CONSTRAINT pk_revisions PRIMARY KEY (id),
    CONSTRAINT fk_revisions_pages FOREIGN KEY (page_id) REFERENCES pages(id),
    CONSTRAINT fk_revisions_users FOREIGN KEY (edited_by) REFERENCES users(id),
    CONSTRAINT uq_revisions_page_id UNIQUE (page_id, revision_number)           -- Also acts as an idx
) ENGINE = InnoDB;

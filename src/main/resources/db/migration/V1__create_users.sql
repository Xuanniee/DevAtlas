CREATE TABLE users (
    id              BIGINT          AUTO_INCREMENT NOT NULL,
    name            VARCHAR(256)    NOT NULL,
    email           VARCHAR(256)    NOT NULL,
    created_at      DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),      -- Current Datetime when created
    modified_at     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3)       -- Current Datetime when created and updated
        ON UPDATE CURRENT_TIMESTAMP(3),

    -- Specify constraints separately to ensure naming consistency
    CONSTRAINT      pk_users        PRIMARY KEY (id),
    CONSTRAINT      uq_user_email   UNIQUE (email)
) ENGINE = InnoDB;
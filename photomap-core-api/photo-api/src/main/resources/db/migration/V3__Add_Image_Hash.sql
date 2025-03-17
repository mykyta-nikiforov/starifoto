CREATE EXTENSION IF NOT EXISTS fuzzystrmatch SCHEMA photo_service;

ALTER TABLE photo_service.image_file ADD COLUMN IF NOT EXISTS image_phash VARCHAR(255);

create table IF NOT EXISTS migration_script_history
(
    id          BIGSERIAL PRIMARY KEY       NOT NULL,
    version     INT                         NOT NULL,
    script_name VARCHAR(255)                NOT NULL,
    executed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
)
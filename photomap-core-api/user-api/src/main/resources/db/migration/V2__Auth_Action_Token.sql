ALTER TABLE user_service.confirm_token RENAME TO auth_action_token;
ALTER TABLE user_service.auth_action_token
    ADD COLUMN IF NOT EXISTS action_type VARCHAR(64) DEFAULT 'CONFIRM_EMAIL';
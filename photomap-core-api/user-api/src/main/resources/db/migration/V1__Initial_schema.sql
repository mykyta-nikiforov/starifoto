CREATE table IF NOT EXISTS user_service.users
(
    id            BIGSERIAL primary key,
    is_enabled    boolean   default true,
    is_non_locked boolean   default true,
    username      varchar(255) not null,
    email         varchar(255) not null UNIQUE,
    password      varchar(255),
    provider      varchar(255) not null,
    created_at    timestamp default current_timestamp,
    updated_at    timestamp default current_timestamp
);

create index IF NOT EXISTS user_email_index
    on user_service.users (email);

create index IF NOT EXISTS user_email_enabled_index
    on user_service.users (email, is_enabled);

CREATE TABLE IF NOT EXISTS user_service.role
(
    id          int primary key,
    name        VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS user_service.users_roles
(
    user_id BIGINT REFERENCES user_service.users (id),
    role_id INT REFERENCES user_service.role (id),
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_service.users (id),
    FOREIGN KEY (role_id) REFERENCES user_service.role (id)

);

CREATE TABLE IF NOT EXISTS user_service.privilege
(
    id   int PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS user_service.roles_privileges
(
    role_id      int REFERENCES user_service.role (id),
    privilege_id int REFERENCES user_service.privilege (id),
    PRIMARY KEY (role_id, privilege_id)
);

INSERT INTO user_service.role (id, name, description)
VALUES (1, 'User', 'Authenticated user, granted after email verification')
ON CONFLICT DO NOTHING;
INSERT INTO user_service.role (id, name, description)
VALUES (2, 'Moderator', 'User with moderation rights')
ON CONFLICT DO NOTHING;
INSERT INTO user_service.role (id, name, description)
VALUES (3, 'Admin', 'User with admin rights')
ON CONFLICT DO NOTHING;

INSERT INTO user_service.privilege (id, name)
VALUES (1, 'ROLE_USER')
ON CONFLICT DO NOTHING;
INSERT INTO user_service.privilege (id, name)
VALUES (2, 'ROLE_MODERATOR')
ON CONFLICT DO NOTHING;
INSERT INTO user_service.privilege (id, name)
VALUES (3, 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;

INSERT INTO user_service.roles_privileges (role_id, privilege_id)
VALUES (1, 1)
ON CONFLICT DO NOTHING;
INSERT INTO user_service.roles_privileges (role_id, privilege_id)
VALUES (2, 2)
ON CONFLICT DO NOTHING;
INSERT INTO user_service.roles_privileges (role_id, privilege_id)
VALUES (3, 3)
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS user_service.confirm_token
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255)                              NOT NULL,
    user_id    BIGINT REFERENCES user_service.users (id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP       NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP       NOT NULL,
    is_used    BOOLEAN   DEFAULT FALSE                   NOT NULL,
    expires_at TIMESTAMP                                 NOT NULL
);

ALTER TABLE user_service.confirm_token
    ADD CONSTRAINT unique_token UNIQUE (token);
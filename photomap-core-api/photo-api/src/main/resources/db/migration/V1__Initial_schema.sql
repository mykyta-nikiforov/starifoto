CREATE table IF NOT EXISTS photo_service.image_file
(
    id         BIGSERIAL primary key,
    name       varchar(255)                        not null,
    url        varchar(255)                        not null,
    height     smallint                            not null,
    width      smallint                            not null,
    file_type  varchar(20)                         not null,
    created_at timestamp default current_timestamp not null,
    updated_at timestamp default current_timestamp not null
);

CREATE table IF NOT EXISTS photo_service.photo
(
    id                      BIGSERIAL primary key,
    title                   varchar(255)                        not null,
    description             varchar(1000)                       not null,
    source                  varchar(255)                        not null,
    author                  varchar(255),
    year_start              smallint                            not null,
    year_end                smallint                            not null,
    license_id              int                                 not null,
    user_id                 bigint                              not null,
    latitude                float8                              not null,
    longitude               float8                              not null,
    is_approximate_location boolean                             not null default false,
    created_at              timestamp default current_timestamp not null,
    updated_at              timestamp default current_timestamp not null
);

CREATE index IF NOT EXISTS photo_user_id_index
    on photo_service.photo (user_id);

CREATE table IF NOT EXISTS photo_service.photo_image_file
(
    photo_id      BIGINT,
    image_file_id BIGINT,
    PRIMARY KEY (photo_id, image_file_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id),
    FOREIGN KEY (image_file_id) REFERENCES image_file (id)
);

CREATE table IF NOT EXISTS photo_service.tag
(
    id         BIGSERIAL primary key,
    name       varchar(255)                        not null,
    created_at timestamp default current_timestamp not null,
    updated_at timestamp default current_timestamp not null
);

CREATE index IF NOT EXISTS tag_name_index
    on photo_service.tag (name);

CREATE table IF NOT EXISTS photo_service.photo_tag
(
    photo_id BIGINT,
    tag_id   BIGINT,
    PRIMARY KEY (photo_id, tag_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);

CREATE TABLE IF NOT EXISTS photo_service.license
(
    id              SERIAL primary key,
    name            VARCHAR(255),
    description     TEXT,
    details_url     VARCHAR(255),
    sequence_number INT,
    active          BOOLEAN   default true,
    created_at      timestamp default current_timestamp not null,
    updated_at      timestamp default current_timestamp not null
);

INSERT INTO photo_service.license (name, description, details_url, sequence_number)
VALUES ('Public Domain',
        'Це ліцензія для творів, авторські права на які вичерпані або не застосовуються, тому вони можуть вільно використовуватися без будь-яких обмежень.',
        'URL_to_Public_Domain_details', 1),
       ('Creative Commons BY-SA',
        'Це ліцензія Creative Commons, яка дозволяє іншим редагувати, змінювати і будувати на основі вашої роботи навіть для комерційних цілей, доки вони кредитують вас і ліцензують свої нові твори за тими ж умовами.',
        'URL_to_CC_BY_SA_details', 2),
       ('Fair-use',
        'Це ліцензія, яка дозволяє використовувати захищені авторським правом матеріали в обмежених і специфічних випадках без дозволу власника авторського права.',
        'URL_to_Fair_use_details', 3);

CREATE TABLE IF NOT EXISTS photo_service.comment
(
    id         BIGSERIAL primary key,
    photo_id   BIGINT                              not null,
    user_id    BIGINT                              not null,
    text       TEXT                                not null,
    created_at timestamp default current_timestamp not null,
    updated_at timestamp default current_timestamp not null,
    FOREIGN KEY (photo_id) REFERENCES photo_service.photo (id)
);

CREATE INDEX IF NOT EXISTS comment_photo_id_index
    ON photo_service.comment (photo_id);
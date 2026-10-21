-- member 테이블
DROP TABLE IF EXISTS member;
CREATE TABLE member
(
    id                      VARCHAR(50) PRIMARY KEY,
    ko_name                 VARCHAR(50),
    en_name                 VARCHAR(50),
    email                   VARCHAR(50),
    phone_number            VARCHAR(50),
    password                VARCHAR(255),
    is_active               BOOLEAN,
    created_at              TIMESTAMP NOT NULL,
    updated_at              TIMESTAMP NOT NULL,
    must_change_password    BOOLEAN NOT NULL DEFAULT false
);

-- code_group 테이블
DROP TABLE IF EXISTS code_group;
-- 시퀀스 생성
CREATE SEQUENCE code_group_sort_order_seq START WITH 1;
CREATE TABLE code_group
(
    id               VARCHAR(50) PRIMARY KEY,
    name             VARCHAR(50)  NOT NULL,
    description      VARCHAR(255) NOT NULL,
    sort_order       INTEGER      NOT NULL DEFAULT nextval('code_group_sort_order_seq'),
    is_active        BOOLEAN      NOT NULL,
    create_member_id VARCHAR(50),
    created_at       TIMESTAMP,
    update_member_id VARCHAR(50),
    updated_at       TIMESTAMP
);

-- code 테이블
DROP TABLE IF EXISTS code;
CREATE TABLE code
(
    id               VARCHAR(50) PRIMARY KEY,
    code_group_id    VARCHAR(50)  NOT NULL,
    message_id       VARCHAR(50)  NOT NULL,
    name             VARCHAR(50)  NOT NULL,
    description      VARCHAR(255) NOT NULL,
    sort_order       INTEGER      NOT NULL,
    is_active        BOOLEAN      NOT NULL,
    create_member_id VARCHAR(50),
    created_at       TIMESTAMP,
    update_member_id VARCHAR(50),
    updated_at       TIMESTAMP,
    CONSTRAINT fk_code_code_group
        FOREIGN KEY (code_group_id) REFERENCES code_group(id) ON DELETE CASCADE
);
CREATE INDEX idx_code_code_group_id ON code USING btree (code_group_id);

-- message 테이블
DROP TABLE IF EXISTS message;
CREATE TABLE message
(
    id               TEXT PRIMARY KEY,
    ko_text          TEXT    NOT NULL,
    en_text          TEXT    NOT NULL,
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    create_member_id VARCHAR(50),
    created_at       TIMESTAMP,
    update_member_id VARCHAR(50),
    updated_at       TIMESTAMP
);

-- menu 테이블
DROP TABLE IF EXISTS menu;
CREATE TABLE menu
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    parent_menu_id   BIGINT,
    message_id       TEXT,
    url              VARCHAR(50),
    name             VARCHAR(50),
    is_active        BOOLEAN DEFAULT true,
    is_delete        BOOLEAN DEFAULT false,
    sort_order       NUMERIC,
    description      TEXT,
    level            NUMERIC,
    create_member_id VARCHAR(50),
    created_at       TIMESTAMP,
    update_member_id VARCHAR(50),
    updated_at       TIMESTAMP
);

-- role 테이블
DROP TABLE IF EXISTS role;
CREATE TABLE role
(
    id               VARCHAR(50) PRIMARY KEY,
    name             VARCHAR(50),
    description      VARCHAR(50),
    is_active        BOOLEAN,
    level            INTEGER,
    create_member_id VARCHAR(50),
    created_at       TIMESTAMP,
    update_member_id VARCHAR(50),
    updated_at       TIMESTAMP
);

-- role_menu 테이블
DROP TABLE IF EXISTS role_menu;
CREATE TABLE role_menu
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_id VARCHAR(50),
    menu_id BIGINT
);

-- role_member 테이블
DROP TABLE IF EXISTS role_member;
CREATE TABLE role_member
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_id   VARCHAR(50),
    member_id VARCHAR(50)
);

-- log_access 테이블
DROP TABLE IF EXISTS log_access;
CREATE TABLE log_access
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id       VARCHAR(50),
    request_url     TEXT,
    request_method  VARCHAR(50),
    request_payload TEXT,
    request_ip      VARCHAR(50),
    response_status INTEGER,
    response_body   TEXT,
    user_agent      TEXT,
    created_at      TIMESTAMP
);

-- log_login 테이블
DROP TABLE IF EXISTS log_login;
CREATE TABLE log_login
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id  VARCHAR(50),
    access_ip  VARCHAR(50),
    user_agent TEXT,
    success    BOOLEAN DEFAULT false,
    created_at TIMESTAMP
);

-- board 테이블
DROP TABLE IF EXISTS board;
CREATE TABLE board
(
    id                   BIGSERIAL PRIMARY KEY,
    attach_file_group_id INT,
    content              TEXT,
    title                VARCHAR(50),
    code_id              VARCHAR(50),
    create_member_id     VARCHAR(50),
    created_at           TIMESTAMP,
    update_member_id     VARCHAR(50),
    updated_at           TIMESTAMP,
    view_count           INT,
    is_pin               BOOLEAN DEFAULT false
);

-- attach_file 테이블
DROP TABLE IF EXISTS attach_file;
CREATE TABLE attach_file
(
    group_id         INT,
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(50),
    save_name        VARCHAR(50),
    sort_order       VARCHAR(50),
    size             VARCHAR(50),
    extension        VARCHAR(50),
    path             VARCHAR(50),
    create_member_id VARCHAR(50),
    created_at       TIMESTAMP,
    update_member_id VARCHAR(50),
    updated_at       TIMESTAMP
);

-- terms 테이블
DROP TABLE IF EXISTS terms;
CREATE TABLE terms
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type         VARCHAR(50) not null,
    version      DECIMAL(3,1) not null,
    content TEXT not null,
    lang_cd      VARCHAR(10),
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- member_terms_agreement 테이블
DROP TABLE IF EXISTS member_terms_agreement;
CREATE TABLE member_terms_agreement
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id   VARCHAR(50),
    terms_id    VARCHAR(50),
    agreed      BOOLEAN DEFAULT false,
    agreed_at   TIMESTAMP
);
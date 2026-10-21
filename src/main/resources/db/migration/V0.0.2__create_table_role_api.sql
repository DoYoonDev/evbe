-- role_api
DROP TABLE IF EXISTS role_api;
CREATE TABLE role_api
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_id     VARCHAR(50),
    url         VARCHAR(255),
    method      VARCHAR(50),
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL
);
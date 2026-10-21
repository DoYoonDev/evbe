-- terms 테이블
DROP TABLE IF EXISTS terms;
CREATE TABLE terms
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type         VARCHAR(50) not null,
    version      DECIMAL(3,1) not null,
    content      TEXT not null,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT now(),
    lang_cd      VARCHAR(10) not null
);
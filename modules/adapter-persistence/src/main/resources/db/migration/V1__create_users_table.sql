CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS users (
    id         uuid        PRIMARY KEY,
    email      citext      NOT NULL,              -- 대소문자 무시
    password   varchar(255),
    nickname   varchar(20),
    provider   varchar(20) NOT NULL,
    role       varchar(20) NOT NULL,
    status     varchar(20) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT chk_users_provider CHECK (provider IN ('LOCAL','GITHUB')),
    CONSTRAINT chk_users_role     CHECK (role IN ('USER','ADMIN')),
    CONSTRAINT chk_users_status   CHECK (status IN ('ACTIVE','INACTIVE','DELETED')),

    -- 로컬 계정이면 비번 필수 (선택)
    CONSTRAINT chk_users_password_local
    CHECK (provider <> 'LOCAL' OR (password IS NOT NULL AND length(password) > 0))
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_created_at_desc ON users (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_users_active_created_at_desc
    ON users (created_at DESC) WHERE status = 'ACTIVE';

-- 닉네임 부분/유사 검색 가속 (pg_trgm)
CREATE INDEX IF NOT EXISTS idx_users_nickname_trgm
    ON users USING GIN (nickname gin_trgm_ops);
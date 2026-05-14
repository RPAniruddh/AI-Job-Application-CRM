-- Drop and recreate user_resumes with TEXT embedding storage
-- We store embeddings as JSON strings and compute similarity in Java
DROP TABLE IF EXISTS user_resumes;

CREATE TABLE user_resumes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    resume_text     TEXT NOT NULL,
    embedding_json  TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_resumes_user_id ON user_resumes(user_id);
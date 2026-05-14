-- Add embedding column to job_applications for JD embeddings
ALTER TABLE job_applications
    ADD COLUMN IF NOT EXISTS jd_embedding vector(1536);

-- Add resume embedding table per user
CREATE TABLE IF NOT EXISTS user_resumes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    resume_text TEXT NOT NULL,
    embedding   vector(1536),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_user_resumes_user_id ON user_resumes(user_id);

-- Index for fast cosine similarity search on JD embeddings
CREATE INDEX IF NOT EXISTS idx_jd_embedding_hnsw
    ON job_applications
    USING hnsw (jd_embedding vector_cosine_ops);
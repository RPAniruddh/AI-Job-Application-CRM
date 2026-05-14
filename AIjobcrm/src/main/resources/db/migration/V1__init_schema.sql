-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Enum types
CREATE TYPE application_stage AS ENUM (
  'WISHLIST',
  'APPLIED',
  'PHONE_SCREEN',
  'INTERVIEWING',
  'OFFER',
  'REJECTED',
  'WITHDRAWN'
);

CREATE TYPE workflow_status AS ENUM (
  'PENDING',
  'WAITING',
  'COMPLETED',
  'CANCELLED'
);

-- Tables
CREATE TABLE users (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email       VARCHAR(255) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,
  full_name   VARCHAR(255),
  created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE job_applications (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  company         VARCHAR(255) NOT NULL,
  role_title      VARCHAR(255) NOT NULL,
  job_url         TEXT,
  raw_description TEXT,
  stage           application_stage NOT NULL DEFAULT 'WISHLIST',
  fit_score       INTEGER,
  notes           TEXT,
  applied_date    DATE,
  created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE contacts (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  application_id  UUID REFERENCES job_applications(id) ON DELETE SET NULL,
  name            VARCHAR(255) NOT NULL,
  title           VARCHAR(255),
  email           VARCHAR(255),
  linkedin_url    TEXT,
  notes           TEXT,
  created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE workflow_tasks (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  application_id  UUID NOT NULL REFERENCES job_applications(id) ON DELETE CASCADE,
  task_type       VARCHAR(100) NOT NULL,
  status          workflow_status NOT NULL DEFAULT 'PENDING',
  scheduled_for   TIMESTAMP NOT NULL,
  executed_at     TIMESTAMP,
  payload         JSONB,
  created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE email_history (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  application_id  UUID NOT NULL REFERENCES job_applications(id) ON DELETE CASCADE,
  user_id         UUID NOT NULL REFERENCES users(id),
  subject         VARCHAR(500),
  body            TEXT,
  recipient_email VARCHAR(255),
  sent_at         TIMESTAMP,
  status          VARCHAR(50) NOT NULL DEFAULT 'DRAFT'
);

-- Indexes
CREATE INDEX idx_applications_user_id ON job_applications(user_id);
CREATE INDEX idx_applications_stage ON job_applications(stage);
CREATE INDEX idx_workflow_tasks_scheduled ON workflow_tasks(scheduled_for, status);

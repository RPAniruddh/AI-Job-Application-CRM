CREATE TABLE email_drafts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    application_id  UUID NOT NULL REFERENCES job_applications(id),
    subject         TEXT NOT NULL,
    body            TEXT NOT NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    sent_at         TIMESTAMP
);

CREATE INDEX idx_email_drafts_user_id ON email_drafts(user_id);
CREATE INDEX idx_email_drafts_application_id ON email_drafts(application_id);
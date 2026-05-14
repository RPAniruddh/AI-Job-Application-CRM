ALTER TABLE workflow_tasks
    ALTER COLUMN payload TYPE TEXT USING payload::TEXT;
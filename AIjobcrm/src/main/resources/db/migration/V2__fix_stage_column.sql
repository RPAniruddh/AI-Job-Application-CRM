ALTER TABLE job_applications 
  ALTER COLUMN stage TYPE VARCHAR(50);

ALTER TABLE workflow_tasks
  ALTER COLUMN status TYPE VARCHAR(50);
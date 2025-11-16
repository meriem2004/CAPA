-- Create or alter document table to add MinIO columns if missing
CREATE TABLE IF NOT EXISTS document (
    id BIGSERIAL PRIMARY KEY,
    capa_id BIGINT NULL,
    file_name VARCHAR(255) NOT NULL,
    s3_bucket VARCHAR(255),
    s3_key VARCHAR(500) NOT NULL,
    s3_version_id VARCHAR(100),
    e_tag VARCHAR(64),
    file_size BIGINT,
    uploaded_by BIGINT,
    uploaded_at TIMESTAMP
);

-- Add columns if table exists but columns missing
ALTER TABLE document ADD COLUMN IF NOT EXISTS s3_bucket VARCHAR(255);
ALTER TABLE document ADD COLUMN IF NOT EXISTS s3_key VARCHAR(500);
ALTER TABLE document ADD COLUMN IF NOT EXISTS s3_version_id VARCHAR(100);
ALTER TABLE document ADD COLUMN IF NOT EXISTS e_tag VARCHAR(64);

-- Optional: drop old file_path column if present
ALTER TABLE document DROP COLUMN IF EXISTS file_path;
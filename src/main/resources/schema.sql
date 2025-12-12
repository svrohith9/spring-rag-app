CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255),
    content_type VARCHAR(128),
    size_bytes BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS document_chunks (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    chunk_index INT NOT NULL,
    text CLOB NOT NULL,
    embedding CLOB NOT NULL,
    CONSTRAINT fk_document FOREIGN KEY (document_id) REFERENCES documents (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_document_chunks_document ON document_chunks(document_id);

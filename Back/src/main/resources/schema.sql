CREATE TABLE IF NOT EXISTS documents (
    id           BIGSERIAL PRIMARY KEY,
    filename     TEXT NOT NULL,
    storage_path TEXT NOT NULL,
    mime_type    TEXT NOT NULL DEFAULT 'application/pdf',
    page_count   INTEGER NOT NULL,
    sha256_hex   CHAR(64),
    uploaded_at  TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS selections (
    id          BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    page_number INTEGER NOT NULL,
    x_norm      DOUBLE PRECISION NOT NULL,
    y_norm      DOUBLE PRECISION NOT NULL,
    w_norm      DOUBLE PRECISION NOT NULL,
    h_norm      DOUBLE PRECISION NOT NULL,
    link_type   TEXT NOT NULL,
    link_value  TEXT NOT NULL
);

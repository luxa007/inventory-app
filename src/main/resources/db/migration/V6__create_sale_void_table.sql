CREATE TABLE IF NOT EXISTS sale_void (
    id BIGSERIAL PRIMARY KEY,
    sale_transaction_id BIGINT NOT NULL REFERENCES sale_transaction(id),
    voided_by VARCHAR(255) NOT NULL,
    voided_at TIMESTAMP NOT NULL DEFAULT NOW(),
    reason VARCHAR(255)
);

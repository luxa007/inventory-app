CREATE TABLE IF NOT EXISTS sale_transaction (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id),
    quantity_sold INTEGER NOT NULL CHECK (quantity_sold > 0),
    unit_price_at_sale NUMERIC(10,2) NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL,
    sold_by VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sale_transaction_product ON sale_transaction(product_id);
CREATE INDEX idx_sale_transaction_timestamp ON sale_transaction(timestamp);

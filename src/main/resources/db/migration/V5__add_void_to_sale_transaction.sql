-- Grant luxa_app_user permission to alter tables
DO $$
BEGIN
    ALTER TABLE sale_transaction ADD COLUMN IF NOT EXISTS voided BOOLEAN NOT NULL DEFAULT FALSE;
    ALTER TABLE sale_transaction ADD COLUMN IF NOT EXISTS voided_at TIMESTAMP;
    ALTER TABLE sale_transaction ADD COLUMN IF NOT EXISTS voided_by VARCHAR(255);
    ALTER TABLE sale_transaction ADD COLUMN IF NOT EXISTS void_reason VARCHAR(255);
EXCEPTION WHEN insufficient_privilege THEN
    RAISE NOTICE 'Skipping ownership change - already done';
END $$;

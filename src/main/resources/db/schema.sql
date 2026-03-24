-- ================================================================
-- Quantity Measurement Application - Database Schema (Production)
-- UC16: Database Integration with JDBC
-- Compatible: MySQL, H2, PostgreSQL
-- ================================================================

-- Quantity Measurements main table
CREATE TABLE IF NOT EXISTS quantity_measurements (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation_type    VARCHAR(50),
    operand1_value    DOUBLE,
    operand1_unit     VARCHAR(50),
    operand1_type     VARCHAR(50),
    operand2_value    DOUBLE,
    operand2_unit     VARCHAR(50),
    operand2_type     VARCHAR(50),
    result_value      DOUBLE,
    result_unit       VARCHAR(50),
    scalar_result     DOUBLE,
    comparison_result BOOLEAN,
    has_error         BOOLEAN,
    error_message     VARCHAR(500),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_operation_type  ON quantity_measurements (operation_type);
CREATE INDEX IF NOT EXISTS idx_operand1_type   ON quantity_measurements (operand1_type);
CREATE INDEX IF NOT EXISTS idx_created_at      ON quantity_measurements (created_at);

-- Quantity Measurement History (audit trail)
CREATE TABLE IF NOT EXISTS quantity_measurement_history (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    measurement_id    BIGINT,
    action            VARCHAR(50),
    performed_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

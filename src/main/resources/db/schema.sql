CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
    id VARCHAR(64) PRIMARY KEY,
    operation_type VARCHAR(32) NOT NULL,
    first_operand_value DOUBLE,
    first_measurement_type VARCHAR(32),
    first_unit VARCHAR(32),
    second_operand_value DOUBLE,
    second_measurement_type VARCHAR(32),
    second_unit VARCHAR(32),
    result_operand_value DOUBLE,
    result_measurement_type VARCHAR(32),
    result_unit VARCHAR(32),
    comparison_result BOOLEAN,
    error_message VARCHAR(1024),
    successful BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_qm_operation ON quantity_measurement_entity(operation_type);
CREATE INDEX IF NOT EXISTS idx_qm_created_at ON quantity_measurement_entity(created_at);
CREATE INDEX IF NOT EXISTS idx_qm_measurement_type ON quantity_measurement_entity(first_measurement_type);

CREATE TABLE IF NOT EXISTS quantity_measurement_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    measurement_id VARCHAR(64) NOT NULL,
    operation_type VARCHAR(32) NOT NULL,
    audit_timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (measurement_id) REFERENCES quantity_measurement_entity(id)
);

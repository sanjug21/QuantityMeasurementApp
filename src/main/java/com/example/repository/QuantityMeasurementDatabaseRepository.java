package com.example.repository;

import com.example.dto.QuantityDTO;
import com.example.entity.QuantityMeasurementEntity;
import com.example.exception.DatabaseException;
import com.example.util.ApplicationConfig;
import com.example.util.ConnectionPool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {
    private static final String INSERT_SQL = "INSERT INTO quantity_measurement_entity ("
            + "id, operation_type, first_value, first_measurement_type, first_unit, "
            + "second_value, second_measurement_type, second_unit, "
            + "result_value, result_measurement_type, result_unit, comparison_result, "
            + "error_message, successful, created_at"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_HISTORY_SQL = "INSERT INTO quantity_measurement_history ("
            + "measurement_id, operation_type, audit_timestamp"
            + ") VALUES (?, ?, ?)";

    private static final String SELECT_ALL_SQL = "SELECT * FROM quantity_measurement_entity ORDER BY created_at ASC";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM quantity_measurement_entity WHERE id = ?";
    private static final String SELECT_BY_OPERATION_SQL = "SELECT * FROM quantity_measurement_entity WHERE operation_type = ? ORDER BY created_at ASC";
    private static final String SELECT_BY_TYPE_SQL = "SELECT * FROM quantity_measurement_entity "
            + "WHERE first_measurement_type = ? OR second_measurement_type = ? OR result_measurement_type = ? "
            + "ORDER BY created_at ASC";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM quantity_measurement_entity";
    private static final String DELETE_HISTORY_SQL = "DELETE FROM quantity_measurement_history";
    private static final String DELETE_ALL_SQL = "DELETE FROM quantity_measurement_entity";

    private final ConnectionPool connectionPool;

    public QuantityMeasurementDatabaseRepository() {
        this(ApplicationConfig.load());
    }

    public QuantityMeasurementDatabaseRepository(ApplicationConfig config) {
        this(new ConnectionPool(config), config.getSchemaLocation());
    }

    QuantityMeasurementDatabaseRepository(ConnectionPool connectionPool, String schemaLocation) {
        this.connectionPool = Objects.requireNonNull(connectionPool, "Connection pool must not be null.");
        initializeSchema(schemaLocation);
    }

    @Override
    public QuantityMeasurementEntity save(QuantityMeasurementEntity entity) {
        Objects.requireNonNull(entity, "QuantityMeasurementEntity must not be null.");

        Connection connection = connectionPool.acquireConnection();
        boolean originalAutoCommit = true;

        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
                statement.setString(1, entity.getId());
                statement.setString(2, entity.getOperationType());
                setQuantity(statement, 3, entity.getFirstOperand());
                setQuantity(statement, 6, entity.getSecondOperand());
                setQuantity(statement, 9, entity.getResult());
                setNullableBoolean(statement, 12, entity.getComparisonResult());
                statement.setString(13, entity.getErrorMessage());
                statement.setBoolean(14, entity.isSuccessful());
                statement.setTimestamp(15, Timestamp.valueOf(entity.getCreatedAt()));
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(INSERT_HISTORY_SQL)) {
                statement.setString(1, entity.getId());
                statement.setString(2, entity.getOperationType());
                statement.setTimestamp(3, Timestamp.valueOf(entity.getCreatedAt()));
                statement.executeUpdate();
            }

            connection.commit();
            return entity;
        } catch (SQLException exception) {
            rollbackQuietly(connection);
            throw new DatabaseException("Unable to persist quantity measurement entity.", exception);
        } finally {
            restoreAutoCommit(connection, originalAutoCommit);
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public List<QuantityMeasurementEntity> findAll() {
        return queryMany(SELECT_ALL_SQL, null);
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return findAll();
    }

    @Override
    public Optional<QuantityMeasurementEntity> findById(String id) {
        Objects.requireNonNull(id, "Id must not be null.");
        List<QuantityMeasurementEntity> entities = queryMany(SELECT_BY_ID_SQL, new StatementBinder() {
            @Override
            public void bind(PreparedStatement statement) throws SQLException {
                statement.setString(1, id);
            }
        });
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(entities.get(0));
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType) {
        Objects.requireNonNull(operationType, "Operation type must not be null.");
        return queryMany(SELECT_BY_OPERATION_SQL, new StatementBinder() {
            @Override
            public void bind(PreparedStatement statement) throws SQLException {
                statement.setString(1, operationType.trim().toUpperCase());
            }
        });
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        Objects.requireNonNull(measurementType, "Measurement type must not be null.");
        return queryMany(SELECT_BY_TYPE_SQL, new StatementBinder() {
            @Override
            public void bind(PreparedStatement statement) throws SQLException {
                String normalizedType = measurementType.trim().toUpperCase();
                statement.setString(1, normalizedType);
                statement.setString(2, normalizedType);
                statement.setString(3, normalizedType);
            }
        });
    }

    @Override
    public long getTotalCount() {
        Connection connection = connectionPool.acquireConnection();
        try (PreparedStatement statement = connection.prepareStatement(COUNT_SQL);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return 0;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to count quantity measurements.", exception);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void clear() {
        deleteAll();
    }

    @Override
    public void deleteAll() {
        Connection connection = connectionPool.acquireConnection();
        boolean originalAutoCommit = true;

        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement historyStatement = connection.prepareStatement(DELETE_HISTORY_SQL)) {
                historyStatement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(DELETE_ALL_SQL)) {
                statement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException exception) {
            rollbackQuietly(connection);
            throw new DatabaseException("Unable to delete quantity measurements.", exception);
        } finally {
            restoreAutoCommit(connection, originalAutoCommit);
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public String getPoolStatistics() {
        return connectionPool.getStatistics();
    }

    @Override
    public void releaseResources() {
        connectionPool.close();
    }

    private List<QuantityMeasurementEntity> queryMany(String sql, StatementBinder statementBinder) {
        Connection connection = connectionPool.acquireConnection();
        List<QuantityMeasurementEntity> entities = new ArrayList<QuantityMeasurementEntity>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (statementBinder != null) {
                statementBinder.bind(statement);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entities.add(mapEntity(resultSet));
                }
            }

            return entities;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to execute database query.", exception);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    private QuantityMeasurementEntity mapEntity(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String operationType = resultSet.getString("operation_type");
        QuantityDTO firstOperand = extractQuantity(resultSet, "first");
        QuantityDTO secondOperand = extractQuantity(resultSet, "second");
        QuantityDTO result = extractQuantity(resultSet, "result");
        Boolean comparisonResult = (Boolean) resultSet.getObject("comparison_result");
        String errorMessage = resultSet.getString("error_message");
        boolean successful = resultSet.getBoolean("successful");
        Timestamp createdAtTimestamp = resultSet.getTimestamp("created_at");
        LocalDateTime createdAt = createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : null;

        return QuantityMeasurementEntity.fromPersisted(
                id,
                operationType,
                firstOperand,
                secondOperand,
                result,
                comparisonResult,
                errorMessage,
                successful,
                createdAt);
    }

    private QuantityDTO extractQuantity(ResultSet resultSet, String prefix) throws SQLException {
        Double value = (Double) resultSet.getObject(prefix + "_value");
        String measurementType = resultSet.getString(prefix + "_measurement_type");
        String unit = resultSet.getString(prefix + "_unit");

        if (value == null || measurementType == null || unit == null) {
            return null;
        }

        return new QuantityDTO(value.doubleValue(), measurementType, unit);
    }

    private void setQuantity(PreparedStatement statement, int startIndex, QuantityDTO quantityDTO) throws SQLException {
        if (quantityDTO == null) {
            statement.setObject(startIndex, null);
            statement.setObject(startIndex + 1, null);
            statement.setObject(startIndex + 2, null);
            return;
        }

        statement.setDouble(startIndex, quantityDTO.getValue());
        statement.setString(startIndex + 1, quantityDTO.getMeasurement());
        statement.setString(startIndex + 2, quantityDTO.getUnitName());
    }

    private void setNullableBoolean(PreparedStatement statement, int index, Boolean value) throws SQLException {
        if (value == null) {
            statement.setObject(index, null);
            return;
        }
        statement.setBoolean(index, value.booleanValue());
    }

    private void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Ignore rollback exception and preserve original failure.
        }
    }

    private void restoreAutoCommit(Connection connection, boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException ignored) {
            // Ignore cleanup exception and avoid masking the original exception.
        }
    }

    private void initializeSchema(String schemaLocation) {
        if (schemaLocation == null || schemaLocation.trim().isEmpty()) {
            return;
        }

        String script = readSchemaScript(schemaLocation);
        if (script.trim().isEmpty()) {
            return;
        }

        Connection connection = connectionPool.acquireConnection();
        try (PreparedStatement statement = connection.prepareStatement(script)) {
            statement.execute();
        } catch (SQLException exception) {
            executeStatementByStatement(connection, script);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    private String readSchemaScript(String schemaLocation) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaLocation);
        if (inputStream == null) {
            throw new DatabaseException("Schema file not found: " + schemaLocation);
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (IOException exception) {
            throw new DatabaseException("Unable to read schema file: " + schemaLocation, exception);
        }
    }

    private void executeStatementByStatement(Connection connection, String script) {
        String[] statements = script.split(";");
        for (String sql : statements) {
            String trimmedSql = sql.trim();
            if (trimmedSql.isEmpty()) {
                continue;
            }
            try (PreparedStatement statement = connection.prepareStatement(trimmedSql)) {
                statement.execute();
            } catch (SQLException exception) {
                throw new DatabaseException("Unable to initialize schema.", exception);
            }
        }
    }

    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}

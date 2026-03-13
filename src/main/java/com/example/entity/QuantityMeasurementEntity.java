package com.example.entity;

import com.example.dto.QuantityDTO;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class QuantityMeasurementEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String operationType;
    private QuantityDTO firstOperand;
    private QuantityDTO secondOperand;
    private QuantityDTO result;
    private Boolean comparisonResult;
    private String errorMessage;
    private boolean successful;
    private LocalDateTime createdAt;

    public QuantityMeasurementEntity(String operationType, QuantityDTO firstOperand, QuantityDTO result) {
        this(operationType, firstOperand, null, result, null, null);
    }

    public QuantityMeasurementEntity(String operationType, QuantityDTO firstOperand, QuantityDTO secondOperand,
            QuantityDTO result) {
        this(operationType, firstOperand, secondOperand, result, null, null);
    }

    public QuantityMeasurementEntity(String operationType, QuantityDTO firstOperand, QuantityDTO secondOperand,
            boolean comparisonResult) {
        this(operationType, firstOperand, secondOperand, null, comparisonResult, null);
    }

    public QuantityMeasurementEntity(String operationType, QuantityDTO firstOperand, String errorMessage) {
        this(operationType, firstOperand, null, null, null, errorMessage);
    }

    public QuantityMeasurementEntity(String operationType, QuantityDTO firstOperand, QuantityDTO secondOperand,
            String errorMessage) {
        this(operationType, firstOperand, secondOperand, null, null, errorMessage);
    }

    private QuantityMeasurementEntity(String operationType, QuantityDTO firstOperand, QuantityDTO secondOperand,
            QuantityDTO result, Boolean comparisonResult, String errorMessage) {
        this.id = UUID.randomUUID().toString();
        this.operationType = Objects.requireNonNull(operationType, "Operation type must not be null.");
        this.firstOperand = copyOf(firstOperand);
        this.secondOperand = copyOf(secondOperand);
        this.result = copyOf(result);
        this.comparisonResult = comparisonResult;
        this.errorMessage = errorMessage;
        this.successful = errorMessage == null || errorMessage.trim().isEmpty();
        this.createdAt = LocalDateTime.now();
    }

    private static QuantityDTO copyOf(QuantityDTO quantityDTO) {
        return quantityDTO == null ? null : new QuantityDTO(quantityDTO);
    }

    public String getId() {
        return id;
    }

    public String getOperationType() {
        return operationType;
    }

    public QuantityDTO getFirstOperand() {
        return copyOf(firstOperand);
    }

    public QuantityDTO getSecondOperand() {
        return copyOf(secondOperand);
    }

    public QuantityDTO getResult() {
        return copyOf(result);
    }

    public Boolean getComparisonResult() {
        return comparisonResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "QuantityMeasurementEntity{"
                + "id='" + id + '\''
                + ", operationType='" + operationType + '\''
                + ", successful=" + successful
                + ", createdAt=" + createdAt
                + '}';
    }
}
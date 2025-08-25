package com.savo.backend.model;

import com.savo.backend.enums.ModelType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "ml_model_versions")
public class MLModelVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Version is required")
    @Column(name = "version", nullable = false, unique = true)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(name = "model_type", nullable = false)
    private ModelType modelType;

    @Min(value = 0, message = "Training data count cannot be negative")
    @Column(name = "training_data_count", nullable = false)
    private Integer trainingDataCount;

    @DecimalMin(value = "0.0", message = "Accuracy score must be between 0 and 1")
    @DecimalMax(value = "1.0", message = "Accuracy score must be between 0 and 1")
    @Column(name = "accuracy_score", nullable = false)
    private Double accuracyScore;

    @Column(name = "deployed_at", nullable = false)
    private LocalDateTime deployedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "model_description", columnDefinition = "TEXT")
    private String modelDescription;

    @Column(name = "training_notes", columnDefinition = "TEXT")
    private String trainingNotes;

    public MLModelVersion() {}

    // Constructor for new model version
    public MLModelVersion(String version, ModelType modelType, Integer trainingDataCount,
                          Double accuracyScore) {
        this.version = version;
        this.modelType = modelType;
        this.trainingDataCount = trainingDataCount;
        this.accuracyScore = accuracyScore;
        this.deployedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isCategorizationModel() {
        return modelType == ModelType.CATEGORIZATION;
    }

    public boolean isPatternDetectionModel() {
        return modelType == ModelType.PATTERN_DETECTION;
    }

    public String getAccuracyPercentage() {
        return String.format("%.1f%%", accuracyScore * 100);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    public Integer getTrainingDataCount() {
        return trainingDataCount;
    }

    public void setTrainingDataCount(Integer trainingDataCount) {
        this.trainingDataCount = trainingDataCount;
    }

    public LocalDateTime getDeployedAt() {
        return deployedAt;
    }

    public void setDeployedAt(LocalDateTime deployedAt) {
        this.deployedAt = deployedAt;
    }

    public Double getAccuracyScore() {
        return accuracyScore;
    }

    public void setAccuracyScore(Double accuracyScore) {
        this.accuracyScore = accuracyScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public String getTrainingNotes() {
        return trainingNotes;
    }

    public void setTrainingNotes(String trainingNotes) {
        this.trainingNotes = trainingNotes;
    }
}

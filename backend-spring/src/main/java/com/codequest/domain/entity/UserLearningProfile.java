package com.codequest.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "user_learning_profile",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "language", "track", "level_id"},
                name = "uk_learning_profile"
        )
)
public class UserLearningProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "track", nullable = false)
    private String track = "beginner";

    @Column(name = "level_id", nullable = false)
    private Integer levelId;

    @Column(name = "attempts")
    private Integer attempts = 0;

    @Column(name = "failed_commands", columnDefinition = "TEXT")
    private String failedCommands;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public UserLearningProfile() {
    }

    public UserLearningProfile(String userId, String language, String track, Integer levelId) {
        this.userId = userId;
        this.language = language;
        this.track = track;
        this.levelId = levelId;
        this.attempts = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public String getFailedCommands() {
        return failedCommands;
    }

    public void setFailedCommands(String failedCommands) {
        this.failedCommands = failedCommands;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}


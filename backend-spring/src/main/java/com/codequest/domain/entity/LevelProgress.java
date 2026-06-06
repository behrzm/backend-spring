package com.codequest.domain.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "level_progress",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "language", "track", "level_id"},
                name = "uk_level_progress"
        )
)
public class LevelProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "track", nullable = false)
    private String track;

    @Column(name = "level_id", nullable = false)
    private Integer levelId;

    @Column(name = "stars")
    private Integer stars = 0;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "saved_code", columnDefinition = "TEXT")
    private String savedCode;

    public LevelProgress() {
    }

    public LevelProgress(String userId, String language, String track, Integer levelId) {
        this.userId = userId;
        this.language = language;
        this.track = track;
        this.levelId = levelId;
        this.stars = 0;
        this.completed = false;
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

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getSavedCode() {
        return savedCode;
    }

    public void setSavedCode(String savedCode) {
        this.savedCode = savedCode;
    }
}


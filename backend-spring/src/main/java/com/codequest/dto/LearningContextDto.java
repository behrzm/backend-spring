package com.codequest.dto;

import com.codequest.domain.entity.UserLearningProfile;
import java.time.OffsetDateTime;

public class LearningContextDto {
    private Long id;
    private String userId;
    private String language;
    private String track;
    private Integer levelId;
    private Integer attempts;
    private String failedCommands;
    private String lastError;
    private OffsetDateTime updatedAt;

    public LearningContextDto() {
    }

    public LearningContextDto(Long id, String userId, String language, String track, Integer levelId,
                             Integer attempts, String failedCommands, String lastError, OffsetDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.language = language;
        this.track = track;
        this.levelId = levelId;
        this.attempts = attempts;
        this.failedCommands = failedCommands;
        this.lastError = lastError;
        this.updatedAt = updatedAt;
    }

    public static LearningContextDto from(UserLearningProfile profile) {
        return new LearningContextDto(
                profile.getId(),
                profile.getUserId(),
                profile.getLanguage(),
                profile.getTrack(),
                profile.getLevelId(),
                profile.getAttempts(),
                profile.getFailedCommands(),
                profile.getLastError(),
                profile.getUpdatedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getTrack() { return track; }
    public void setTrack(String track) { this.track = track; }
    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    public Integer getAttempts() { return attempts; }
    public void setAttempts(Integer attempts) { this.attempts = attempts; }
    public String getFailedCommands() { return failedCommands; }
    public void setFailedCommands(String failedCommands) { this.failedCommands = failedCommands; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}



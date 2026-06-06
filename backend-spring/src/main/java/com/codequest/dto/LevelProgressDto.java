package com.codequest.dto;

import com.codequest.domain.entity.LevelProgress;
import java.time.OffsetDateTime;

public class LevelProgressDto {
    private Long id;
    private String userId;
    private String language;
    private String track;
    private Integer levelId;
    private Integer stars;
    private Boolean completed;
    private String completedAt;
    private String savedCode;

    public LevelProgressDto() {}

    public static LevelProgressDto from(LevelProgress entity) {
        LevelProgressDto dto = new LevelProgressDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setLanguage(entity.getLanguage());
        dto.setTrack(entity.getTrack());
        dto.setLevelId(entity.getLevelId());
        dto.setStars(entity.getStars());
        dto.setCompleted(entity.getCompleted());
        // Используем completedAt из сущности
        dto.setCompletedAt(entity.getCompletedAt() != null ? entity.getCompletedAt().toString() : null);
        dto.setSavedCode(entity.getSavedCode());
        return dto;
    }

    // Getters and Setters
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
    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public String getSavedCode() { return savedCode; }
    public void setSavedCode(String savedCode) { this.savedCode = savedCode; }
}

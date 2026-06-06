package com.codequest.dto;

public class UpdateLearningContextRequest {
    private String language;
    private String track;
    private Integer levelId;
    private String failedCommands;
    private String lastError;

    public UpdateLearningContextRequest() {
    }

    public UpdateLearningContextRequest(String language, String track, Integer levelId,
                                       String failedCommands, String lastError) {
        this.language = language;
        this.track = track;
        this.levelId = levelId;
        this.failedCommands = failedCommands;
        this.lastError = lastError;
    }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getTrack() { return track; }
    public void setTrack(String track) { this.track = track; }
    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    public String getFailedCommands() { return failedCommands; }
    public void setFailedCommands(String failedCommands) { this.failedCommands = failedCommands; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
}

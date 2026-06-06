package com.codequest.dto;

public class UpdateLevelProgressRequest {
    private String language;
    private String track;
    private Integer levelId;
    private Integer stars;
    private Boolean completed;
    private String solutionCode;

    public UpdateLevelProgressRequest() {}

    public UpdateLevelProgressRequest(String language, String track, Integer levelId, Integer stars, Boolean completed, String solutionCode) {
        this.language = language;
        this.track = track;
        this.levelId = levelId;
        this.stars = stars;
        this.completed = completed;
        this.solutionCode = solutionCode;
    }

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
    public String getSolutionCode() { return solutionCode; }
    public void setSolutionCode(String solutionCode) { this.solutionCode = solutionCode; }
}

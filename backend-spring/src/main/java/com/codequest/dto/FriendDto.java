package com.codequest.dto;

public class FriendDto {
    private String id;
    private String displayName;
    private Integer xp;
    private Integer level;
    private Integer elo;
    private Integer streak;
    private Integer wins;
    private String avatarUrl;
    private String status;
    private String learningLanguage;

    public FriendDto() {
    }

    // Конструктор на 9 параметров для FriendService
    public FriendDto(String id, String displayName, Integer xp, Integer level, Integer elo, 
                     Integer streak, Integer wins, String avatarUrl, String status) {
        this.id = id;
        this.displayName = displayName;
        this.xp = xp;
        this.level = level;
        this.elo = elo;
        this.streak = streak;
        this.wins = wins;
        this.avatarUrl = avatarUrl;
        this.status = status;
    }

    public FriendDto(String id, String displayName, Integer xp, Integer level, Integer elo, 
                     Integer streak, Integer wins, String avatarUrl, String status, String learningLanguage) {
        this(id, displayName, xp, level, elo, streak, wins, avatarUrl, status);
        this.learningLanguage = learningLanguage;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Integer getXp() { return xp; }
    public void setXp(Integer xp) { this.xp = xp; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getElo() { return elo; }
    public void setElo(Integer elo) { this.elo = elo; }
    public Integer getStreak() { return streak; }
    public void setStreak(Integer streak) { this.streak = streak; }
    public Integer getWins() { return wins; }
    public void setWins(Integer wins) { this.wins = wins; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLearningLanguage() { return learningLanguage; }
    public void setLearningLanguage(String learningLanguage) { this.learningLanguage = learningLanguage; }
}

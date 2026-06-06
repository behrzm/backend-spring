package com.codequest.dto;

public class LeaderboardEntryDto {
    private String id;
    private String displayName;
    private Integer xp;
    private Integer level;
    private Integer wins;

    public LeaderboardEntryDto() {
    }

    public LeaderboardEntryDto(String id, String displayName, Integer xp, Integer level, Integer wins) {
        this.id = id;
        this.displayName = displayName;
        this.xp = xp;
        this.level = level;
        this.wins = wins;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }
}

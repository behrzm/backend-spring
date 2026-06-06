package com.codequest.dto;

import com.codequest.domain.entity.Profile;
import java.time.OffsetDateTime;
import java.time.LocalDate;

public class ProfileDto {
    private String id;
    private String email;
    private String displayName;
    private Integer xp;
    private Integer level;
    private Integer streak;
    private Integer wins;
    private Integer elo;
    private String avatarUrl;
    private LocalDate lastRewardClaimDate;
    private LocalDate lastDailyChallengeDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ProfileDto() {
    }

    public ProfileDto(String id, String email, String displayName, Integer xp, Integer level,
                      Integer streak, Integer wins, Integer elo, String avatarUrl, LocalDate lastRewardClaimDate,
                      LocalDate lastDailyChallengeDate,
                      OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.xp = xp;
        this.level = level;
        this.streak = streak;
        this.wins = wins;
        this.elo = elo;
        this.avatarUrl = avatarUrl;
        this.lastRewardClaimDate = lastRewardClaimDate;
        this.lastDailyChallengeDate = lastDailyChallengeDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProfileDto from(Profile profile) {
        return new ProfileDto(
                profile.getId(),
                profile.getEmail(),
                profile.getDisplayName(),
                profile.getXp(),
                profile.getLevel(),
                profile.getStreak(),
                profile.getWins(),
                profile.getElo() != null ? profile.getElo() : 1000,
                profile.getAvatarUrl(),
                profile.getLastRewardClaimDate(),
                profile.getLastDailyChallengeDate(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Integer getXp() { return xp; }
    public void setXp(Integer xp) { this.xp = xp; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getStreak() { return streak; }
    public void setStreak(Integer streak) { this.streak = streak; }
    public Integer getWins() { return wins; }
    public void setWins(Integer wins) { this.wins = wins; }
    public Integer getElo() { return elo; }
    public void setElo(Integer elo) { this.elo = elo; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public LocalDate getLastRewardClaimDate() { return lastRewardClaimDate; }
    public void setLastRewardClaimDate(LocalDate lastRewardClaimDate) { this.lastRewardClaimDate = lastRewardClaimDate; }
    public LocalDate getLastDailyChallengeDate() { return lastDailyChallengeDate; }
    public void setLastDailyChallengeDate(LocalDate lastDailyChallengeDate) { this.lastDailyChallengeDate = lastDailyChallengeDate; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

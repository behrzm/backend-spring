package com.codequest.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "xp")
    private Integer xp = 0;

    @Column(name = "level")
    private Integer level = 1;

    @Column(name = "streak")
    private Integer streak = 0;

    @Column(name = "wins")
    private Integer wins = 0;

    @Column(name = "elo")
    private Integer elo = 1000;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "last_reward_claim_date")
    private LocalDate lastRewardClaimDate;

    @Column(name = "last_daily_challenge_date")
    private LocalDate lastDailyChallengeDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Profile() {
    }

    public Profile(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.xp = 0;
        this.level = 1;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getElo() {
        return elo;
    }

    public void setElo(Integer elo) {
        this.elo = elo;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public LocalDate getLastRewardClaimDate() {
        return lastRewardClaimDate;
    }

    public void setLastRewardClaimDate(LocalDate lastRewardClaimDate) {
        this.lastRewardClaimDate = lastRewardClaimDate;
    }

    public LocalDate getLastDailyChallengeDate() {
        return lastDailyChallengeDate;
    }

    public void setLastDailyChallengeDate(LocalDate lastDailyChallengeDate) {
        this.lastDailyChallengeDate = lastDailyChallengeDate;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

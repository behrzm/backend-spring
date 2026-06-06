package com.codequest.service;

import com.codequest.domain.entity.Profile;
import com.codequest.domain.entity.XpHistory;
import com.codequest.dto.*;
import com.codequest.repository.FriendshipRepository;
import com.codequest.repository.ProfileRepository;
import com.codequest.repository.XpHistoryRepository;
import com.codequest.security.SecurityContext;
import com.codequest.util.LevelProgression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    private final ProfileRepository profileRepository;
    private final XpHistoryRepository xpHistoryRepository;
    private final FriendshipRepository friendshipRepository;

    public ProfileService(ProfileRepository profileRepository, 
                          XpHistoryRepository xpHistoryRepository,
                          FriendshipRepository friendshipRepository) {
        this.profileRepository = profileRepository;
        this.xpHistoryRepository = xpHistoryRepository;
        this.friendshipRepository = friendshipRepository;
    }

    public ProfileDto getMyProfile() {
        String userId = SecurityContext.getUserId();
        Profile profile = profileRepository.findById(userId).orElseGet(() -> {
            Profile p = new Profile(userId, null, "Cyber Cadet");
            p.setXp(0);
            p.setLevel(1);
            p.setElo(1000);
            p.setStreak(0);
            p.setWins(0);
            return p;
        });

        syncLevelFromXp(profile);
        updateStreak(profile);
        profileRepository.save(profile);
        return ProfileDto.from(profile);
    }

    private void syncLevelFromXp(Profile profile) {
        int xp = profile.getXp() != null ? profile.getXp() : 0;
        profile.setLevel(LevelProgression.levelFromTotalXp(xp));
    }

    private void updateStreak(Profile profile) {
        LocalDate today = LocalDate.now();
        LocalDate lastActive = profile.getLastActiveDate();

        if (lastActive == null) {
            profile.setStreak(1);
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastActive, today);
            if (daysBetween == 1) {
                profile.setStreak(profile.getStreak() + 1);
            } else if (daysBetween > 1) {
                profile.setStreak(1);
            }
        }
        profile.setLastActiveDate(today);
    }

    public ProfileDto addXp(AddXpRequest request) {
        String userId = SecurityContext.getUserId();
        Profile profile = profileRepository.findById(userId).orElseThrow();
        return performAddXp(profile, request.getDeltaXp(), request.getReason());
    }

    private ProfileDto performAddXp(Profile profile, int amount, String reason) {
        int currentXp = (profile.getXp() != null) ? profile.getXp() : 0;
        int newXp = currentXp + amount;
        profile.setXp(newXp);
        profile.setLevel(LevelProgression.levelFromTotalXp(newXp));
        profile.setUpdatedAt(OffsetDateTime.now());
        
        updateStreak(profile);
        profileRepository.save(profile);
        xpHistoryRepository.save(new XpHistory(profile.getId(), amount, reason));
        
        return ProfileDto.from(profile);
    }

    public ProfileDto updateElo(int delta) {
        String userId = SecurityContext.getUserId();
        Profile profile = profileRepository.findById(userId).orElseThrow();
        profile.setElo(Math.max(0, (profile.getElo() != null ? profile.getElo() : 1000) + delta));
        if (delta > 0) {
            profile.setWins((profile.getWins() != null ? profile.getWins() : 0) + 1);
        }
        profileRepository.save(profile);
        return ProfileDto.from(profile);
    }

    public ProfileDto addWins(AddWinsRequest request) {
        String userId = SecurityContext.getUserId();
        Profile profile = profileRepository.findById(userId).orElseThrow();
        int currentWins = (profile.getWins() != null) ? profile.getWins() : 0;
        profile.setWins(currentWins + request.getDeltaWins());
        profileRepository.save(profile);
        return ProfileDto.from(profile);
    }

    public ProfileDto claimDailyReward() {
        String userId = SecurityContext.getUserId();
        Profile profile = profileRepository.findById(userId).orElseThrow();
        LocalDate today = LocalDate.now();
        if (today.equals(profile.getLastRewardClaimDate())) {
            return ProfileDto.from(profile);
        }
        profile.setLastRewardClaimDate(today);
        return performAddXp(profile, 120, "daily_reward");
    }

    public ProfileDto completeDailyChallenge() {
        String userId = SecurityContext.getUserId();
        Profile profile = profileRepository.findById(userId).orElseThrow();
        LocalDate today = LocalDate.now();
        if (today.equals(profile.getLastDailyChallengeDate())) {
            throw new IllegalStateException("Daily challenge already completed today");
        }
        profile.setLastDailyChallengeDate(today);
        return performAddXp(profile, 200, "daily_challenge");
    }

    public List<LeaderboardEntryDto> getTopPlayers(Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, Math.min(limit, 100));
        return profileRepository.findTopPlayers(pageRequest).stream()
                .map(p -> new LeaderboardEntryDto(p.getId(), p.getDisplayName(), p.getXp(), p.getLevel(), p.getWins()))
                .collect(Collectors.toList());
    }

    public ProfileDto updateMyProfile(UpdateProfileRequest request) {
        String userId = SecurityContext.getUserId();
        Profile profile = profileRepository.findById(userId).orElseThrow();
        if (request.getDisplayName() != null) profile.setDisplayName(request.getDisplayName());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        profileRepository.save(profile);
        return ProfileDto.from(profile);
    }

    public List<XpHistoryDto> getXpHistory(Integer limit) {
        String userId = SecurityContext.getUserId();
        PageRequest pageRequest = PageRequest.of(0, Math.min(limit, 100));
        return xpHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageRequest).stream()
                .map(XpHistoryDto::from)
                .collect(Collectors.toList());
    }
}

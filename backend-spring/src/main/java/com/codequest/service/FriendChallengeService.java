package com.codequest.service;

import com.codequest.domain.entity.Profile;
import com.codequest.dto.CreateFriendChallengeRequest;
import com.codequest.dto.FriendChallengeDto;
import com.codequest.dto.WarMatchResponse;
import com.codequest.repository.ProfileRepository;
import com.codequest.security.SecurityContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FriendChallengeService {

    private static final long TTL_MS = 5 * 60 * 1000L;

    private final ProfileRepository profileRepository;
    private final Map<String, StoredChallenge> challenges = new ConcurrentHashMap<>();

    public FriendChallengeService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public FriendChallengeDto createChallenge(CreateFriendChallengeRequest request) {
        purgeExpired();
        String userId = SecurityContext.getUserId();
        Profile challenger = profileRepository.findById(userId).orElseThrow();
        Profile target = profileRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new RuntimeException("Player not found"));

        if (userId.equals(target.getId())) {
            throw new RuntimeException("Cannot challenge yourself");
        }

        String challengeId = UUID.randomUUID().toString();
        StoredChallenge stored = new StoredChallenge(
                challengeId,
                userId,
                target.getId(),
                request.getLanguage(),
                "PENDING",
                Instant.now().toEpochMilli(),
                null
        );
        challenges.put(challengeId, stored);

        return toDto(stored, challenger);
    }

    public List<FriendChallengeDto> getIncomingChallenges() {
        purgeExpired();
        String userId = SecurityContext.getUserId();
        List<FriendChallengeDto> result = new ArrayList<>();
        for (StoredChallenge c : challenges.values()) {
            if (c.targetId.equals(userId) && "PENDING".equals(c.status)) {
                Profile challenger = profileRepository.findById(c.challengerId).orElse(null);
                if (challenger != null) {
                    result.add(toDto(c, challenger));
                }
            }
        }
        return result;
    }

    public FriendChallengeDto getChallenge(String challengeId) {
        purgeExpired();
        StoredChallenge c = challenges.get(challengeId);
        if (c == null) throw new RuntimeException("Challenge not found");
        Profile challenger = profileRepository.findById(c.challengerId).orElseThrow();
        return toDto(c, challenger);
    }

    public WarMatchResponse acceptChallenge(String challengeId) {
        purgeExpired();
        String userId = SecurityContext.getUserId();
        StoredChallenge c = challenges.get(challengeId);
        if (c == null) throw new RuntimeException("Challenge not found");
        if (!c.targetId.equals(userId)) throw new RuntimeException("Not your challenge");
        if (!"PENDING".equals(c.status)) throw new RuntimeException("Challenge is no longer pending");

        Profile challenger = profileRepository.findById(c.challengerId).orElseThrow();
        Profile target = profileRepository.findById(c.targetId).orElseThrow();

        long opponentTimeMs = 22_000L + (long) (Math.random() * 55_000L);
        String matchId = UUID.randomUUID().toString();
        c.status = "ACTIVE";
        c.matchId = matchId;
        c.opponentSolveTimeMs = opponentTimeMs;

        return new WarMatchResponse(
                matchId,
                challenger.getDisplayName() != null ? challenger.getDisplayName() : "Friend",
                challenger.getElo() != null ? challenger.getElo() : 1000,
                missionForLanguage(c.language),
                opponentTimeMs,
                target.getElo() != null ? target.getElo() : 1000,
                false
        );
    }

    public WarMatchResponse getActiveMatchForChallenger(String challengeId) {
        purgeExpired();
        String userId = SecurityContext.getUserId();
        StoredChallenge c = challenges.get(challengeId);
        if (c == null) throw new RuntimeException("Challenge not found");
        if (!c.challengerId.equals(userId)) throw new RuntimeException("Not your challenge");
        if (!"ACTIVE".equals(c.status) || c.matchId == null) {
            throw new RuntimeException("Challenge not accepted yet");
        }

        Profile target = profileRepository.findById(c.targetId).orElseThrow();
        Profile challenger = profileRepository.findById(c.challengerId).orElseThrow();

        return new WarMatchResponse(
                c.matchId,
                target.getDisplayName() != null ? target.getDisplayName() : "Friend",
                target.getElo() != null ? target.getElo() : 1000,
                missionForLanguage(c.language),
                c.opponentSolveTimeMs,
                challenger.getElo() != null ? challenger.getElo() : 1000,
                false
        );
    }

    public void declineChallenge(String challengeId) {
        String userId = SecurityContext.getUserId();
        StoredChallenge c = challenges.get(challengeId);
        if (c != null && (c.targetId.equals(userId) || c.challengerId.equals(userId))) {
            challenges.remove(challengeId);
        }
    }

    private void purgeExpired() {
        long now = Instant.now().toEpochMilli();
        challenges.entrySet().removeIf(e -> now - e.getValue().createdAtMs > TTL_MS);
    }

    private static int missionForLanguage(String lang) {
        if (lang == null) return 1;
        return switch (lang.toLowerCase()) {
            case "javascript" -> 2;
            case "kotlin" -> 3;
            case "java" -> 4;
            default -> 1;
        };
    }

    private FriendChallengeDto toDto(StoredChallenge c, Profile challenger) {
        return new FriendChallengeDto(
                c.challengeId,
                c.challengerId,
                challenger.getDisplayName(),
                c.targetId,
                c.language,
                c.status,
                challenger.getElo(),
                challenger.getXp()
        );
    }

    private static final class StoredChallenge {
        final String challengeId;
        final String challengerId;
        final String targetId;
        final String language;
        String status;
        final long createdAtMs;
        String matchId;
        long opponentSolveTimeMs = 30_000L;

        StoredChallenge(
                String challengeId,
                String challengerId,
                String targetId,
                String language,
                String status,
                long createdAtMs,
                String matchId
        ) {
            this.challengeId = challengeId;
            this.challengerId = challengerId;
            this.targetId = targetId;
            this.language = language;
            this.status = status;
            this.createdAtMs = createdAtMs;
            this.matchId = matchId;
        }
    }
}

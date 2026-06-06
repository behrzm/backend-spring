package com.codequest.service;

import com.codequest.domain.entity.Profile;
import com.codequest.dto.WarMatchResponse;
import com.codequest.dto.WarResultRequest;
import com.codequest.dto.WarResultResponse;
import com.codequest.repository.ProfileRepository;
import com.codequest.security.SecurityContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class WarMatchmakingService {

    private static final int DEFAULT_ELO = 1000;
    private static final int ELO_DELTA = 25;
    private static final long QUEUE_TTL_MS = 45_000L;

    private final ProfileRepository profileRepository;
    private final ProfileService profileService;
    private final Map<String, Queue<WaitingPlayer>> queues = new ConcurrentHashMap<>();
    private final Map<String, ActiveMatch> matches = new ConcurrentHashMap<>();

    public WarMatchmakingService(ProfileRepository profileRepository, ProfileService profileService) {
        this.profileRepository = profileRepository;
        this.profileService = profileService;
    }

    public WarMatchResponse findOpponent(String language) {
        String userId = SecurityContext.getUserId();
        Profile me = profileRepository.findById(userId).orElseThrow();
        String langKey = normalizeLanguage(language);
        Queue<WaitingPlayer> queue = queues.computeIfAbsent(langKey, k -> new ConcurrentLinkedQueue<>());

        purgeExpired(queue);

        WaitingPlayer opponent = null;
        for (WaitingPlayer candidate : queue) {
            if (!candidate.userId.equals(userId)) {
                opponent = candidate;
                queue.remove(candidate);
                break;
            }
        }

        int myElo = me.getElo() != null ? me.getElo() : DEFAULT_ELO;
        int missionId = missionForLanguage(langKey);

        if (opponent != null) {
            Profile oppProfile = profileRepository.findById(opponent.userId).orElse(null);
            String oppName = oppProfile != null && oppProfile.getDisplayName() != null
                    ? oppProfile.getDisplayName() : "Cyber Rival";
            int oppElo = oppProfile != null && oppProfile.getElo() != null ? oppProfile.getElo() : DEFAULT_ELO;
            long oppTimeMs = opponent.estimatedSolveTimeMs;

            String matchId = UUID.randomUUID().toString();
            matches.put(matchId, new ActiveMatch(matchId, userId, opponent.userId, langKey, oppTimeMs, false));

            return new WarMatchResponse(
                    matchId,
                    oppName,
                    oppElo,
                    missionId,
                    oppTimeMs,
                    myElo,
                    false
            );
        }

        long estimatedMs = 18_000L + (long) (Math.random() * 52_000L);
        queue.offer(new WaitingPlayer(userId, Instant.now().toEpochMilli(), estimatedMs));

        String botName = botNameFor(langKey);
        int botElo = Math.max(800, myElo + (int) (Math.random() * 201) - 100);
        long botTimeMs = 20_000L + (long) (Math.random() * 70_000L);
        String matchId = UUID.randomUUID().toString();
        matches.put(matchId, new ActiveMatch(matchId, userId, null, langKey, botTimeMs, true));

        return new WarMatchResponse(matchId, botName, botElo, missionId, botTimeMs, myElo, true);
    }

    public WarResultResponse reportResult(WarResultRequest request) {
        String userId = SecurityContext.getUserId();
        ActiveMatch match = matches.remove(request.getMatchId());
        if (match == null) {
            match = new ActiveMatch(request.getMatchId(), userId, null, request.getLanguage(), 30_000L, true);
        }

        long playerMs = request.getSolveTimeMs() != null ? request.getSolveTimeMs() : Long.MAX_VALUE;
        long opponentMs = match.opponentSolveTimeMs;
        boolean won = request.getWon() != null
                ? request.getWon()
                : playerMs < opponentMs;

        int delta = won ? ELO_DELTA : -ELO_DELTA;
        var profile = profileService.updateElo(delta);

        return new WarResultResponse(
                won,
                delta,
                profile.getElo() != null ? profile.getElo() : DEFAULT_ELO,
                profile.getWins() != null ? profile.getWins() : 0,
                formatMs(playerMs),
                formatMs(opponentMs)
        );
    }

    private void purgeExpired(Queue<WaitingPlayer> queue) {
        long now = Instant.now().toEpochMilli();
        queue.removeIf(p -> now - p.enqueuedAtMs > QUEUE_TTL_MS);
    }

    private static String normalizeLanguage(String language) {
        if (language == null) return "python";
        String l = language.trim().toLowerCase();
        if (l.contains("java") && !l.contains("script")) return "java";
        if (l.contains("script")) return "javascript";
        if (l.contains("kotlin")) return "kotlin";
        if (l.contains("python")) return "python";
        return l;
    }

    private static int missionForLanguage(String langKey) {
        return switch (langKey) {
            case "javascript" -> 2;
            case "kotlin" -> 3;
            case "java" -> 4;
            default -> 1;
        };
    }

    private static String botNameFor(String langKey) {
        String[] names = {"NeonRival", "ByteHunter", "StackPhantom", "CodeReaper", "SyntaxLord"};
        int idx = Math.abs(langKey.hashCode()) % names.length;
        return names[idx];
    }

    private static String formatMs(long ms) {
        if (ms == Long.MAX_VALUE || ms < 0) ms = 0;
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long millis = (ms % 1000) / 10;
        return String.format("%02d:%02d.%02d", minutes, seconds, millis);
    }

    private record WaitingPlayer(String userId, long enqueuedAtMs, long estimatedSolveTimeMs) {
    }

    private record ActiveMatch(
            String matchId,
            String playerId,
            String opponentId,
            String language,
            long opponentSolveTimeMs,
            boolean bot
    ) {
    }
}

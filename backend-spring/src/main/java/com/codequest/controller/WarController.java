package com.codequest.controller;

import com.codequest.dto.*;
import com.codequest.service.FriendChallengeService;
import com.codequest.service.WarMatchmakingService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/war")
@Tag(name = "Coding War", description = "PvP coding battles and ELO")
@SecurityRequirement(name = "bearerAuth")
public class WarController {

    private final WarMatchmakingService warMatchmakingService;
    private final FriendChallengeService friendChallengeService;

    public WarController(
            WarMatchmakingService warMatchmakingService,
            FriendChallengeService friendChallengeService
    ) {
        this.warMatchmakingService = warMatchmakingService;
        this.friendChallengeService = friendChallengeService;
    }

    @PostMapping("/find-opponent")
    @Operation(summary = "Find an online opponent for the selected language")
    public ResponseEntity<WarMatchResponse> findOpponent(@RequestParam String language) {
        return ResponseEntity.ok(warMatchmakingService.findOpponent(language));
    }

    @PostMapping("/report-result")
    @Operation(summary = "Report battle result and update ELO / wins")
    public ResponseEntity<WarResultResponse> reportResult(@Valid @RequestBody WarResultRequest request) {
        return ResponseEntity.ok(warMatchmakingService.reportResult(request));
    }

    @PostMapping("/friend-challenge")
    @Operation(summary = "Challenge a friend to a coding duel")
    public ResponseEntity<FriendChallengeDto> createFriendChallenge(
            @Valid @RequestBody CreateFriendChallengeRequest request
    ) {
        return ResponseEntity.ok(friendChallengeService.createChallenge(request));
    }

    @GetMapping("/friend-challenges/incoming")
    @Operation(summary = "Incoming friend duel invitations")
    public ResponseEntity<List<FriendChallengeDto>> incomingFriendChallenges() {
        return ResponseEntity.ok(friendChallengeService.getIncomingChallenges());
    }

    @GetMapping("/friend-challenge/{challengeId}")
    @Operation(summary = "Get friend challenge status")
    public ResponseEntity<FriendChallengeDto> getFriendChallenge(@PathVariable String challengeId) {
        return ResponseEntity.ok(friendChallengeService.getChallenge(challengeId));
    }

    @PostMapping("/friend-challenge/{challengeId}/accept")
    @Operation(summary = "Accept friend duel and start match")
    public ResponseEntity<WarMatchResponse> acceptFriendChallenge(@PathVariable String challengeId) {
        return ResponseEntity.ok(friendChallengeService.acceptChallenge(challengeId));
    }

    @GetMapping("/friend-challenge/{challengeId}/match")
    @Operation(summary = "Get match after friend accepted (for challenger)")
    public ResponseEntity<WarMatchResponse> challengerMatch(@PathVariable String challengeId) {
        return ResponseEntity.ok(friendChallengeService.getActiveMatchForChallenger(challengeId));
    }

    @PostMapping("/friend-challenge/{challengeId}/decline")
    @Operation(summary = "Decline friend duel")
    public ResponseEntity<Void> declineFriendChallenge(@PathVariable String challengeId) {
        friendChallengeService.declineChallenge(challengeId);
        return ResponseEntity.ok().build();
    }
}

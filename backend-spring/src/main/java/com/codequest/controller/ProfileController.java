package com.codequest.controller;

import com.codequest.dto.*;
import com.codequest.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/profiles")
@Tag(name = "Profiles", description = "User profile operations")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ProfileDto> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @GetMapping("/top")
    @Operation(summary = "Get top players")
    public ResponseEntity<List<LeaderboardEntryDto>> getTopPlayers(
            @RequestParam(defaultValue = "25") Integer limit) {
        return ResponseEntity.ok(profileService.getTopPlayers(limit));
    }

    @PutMapping("/me")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ProfileDto> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateMyProfile(request));
    }

    @PostMapping("/me/xp")
    @Operation(summary = "Add XP to current user")
    public ResponseEntity<ProfileDto> addXp(@RequestBody AddXpRequest request) {
        return ResponseEntity.ok(profileService.addXp(request));
    }

    @PostMapping("/me/wins")
    @Operation(summary = "Add wins to current user")
    public ResponseEntity<ProfileDto> addWins(@RequestBody AddWinsRequest request) {
        return ResponseEntity.ok(profileService.addWins(request));
    }

    @PostMapping("/me/elo")
    @Operation(summary = "Update ELO rating (e.g. +25 win / -25 loss in Coding War)")
    public ResponseEntity<ProfileDto> updateElo(@RequestParam int delta) {
        return ResponseEntity.ok(profileService.updateElo(delta));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Leaderboard top players by XP")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(
            @RequestParam(defaultValue = "20") Integer limit) {
        return ResponseEntity.ok(profileService.getTopPlayers(limit));
    }

    @PostMapping("/me/claim-reward")
    @Operation(summary = "Claim daily reward")
    public ResponseEntity<ProfileDto> claimDailyReward() {
        return ResponseEntity.ok(profileService.claimDailyReward());
    }

    @PostMapping("/me/daily-challenge")
    @Operation(summary = "Complete daily challenge (once per calendar day)")
    public ResponseEntity<?> completeDailyChallenge() {
        try {
            return ResponseEntity.ok(profileService.completeDailyChallenge());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me/xp-history")
    @Operation(summary = "Get XP history")
    public ResponseEntity<List<XpHistoryDto>> getXpHistory(
            @RequestParam(defaultValue = "20") Integer limit) {
        return ResponseEntity.ok(profileService.getXpHistory(limit));
    }
}

package com.codequest.controller

import com.codequest.dto.*
import com.codequest.service.ProfileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/profiles")
@Tag(name = "Profiles", description = "User profile operations")
@SecurityRequirement(name = "bearerAuth")
class ProfileController(
    private val profileService: ProfileService
) {

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the profile of the authenticated user")
    fun getMyProfile(): ResponseEntity<ProfileDto> {
        return ResponseEntity.ok(profileService.getMyProfile())
    }

    @GetMapping("/top")
    @Operation(summary = "Get top players", description = "Returns the leaderboard with top players by XP")
    fun getTopPlayers(
        @RequestParam(defaultValue = "25") limit: Int
    ): ResponseEntity<List<LeaderboardEntryDto>> {
        return ResponseEntity.ok(profileService.getTopPlayers(limit))
    }

    @PutMapping("/me")
    @Operation(summary = "Update user profile", description = "Updates display name and/or avatar URL")
    fun updateMyProfile(@RequestBody request: UpdateProfileRequest): ResponseEntity<ProfileDto> {
        return ResponseEntity.ok(profileService.updateMyProfile(request))
    }

    @PostMapping("/me/xp")
    @Operation(summary = "Add XP to current user", description = "Atomically increases XP and recalculates level")
    fun addXp(@RequestBody request: AddXpRequest): ResponseEntity<ProfileDto> {
        return ResponseEntity.ok(profileService.addXp(request))
    }

    @PostMapping("/me/wins")
    @Operation(summary = "Add wins to current user", description = "Increases the win counter")
    fun addWins(@RequestBody request: AddWinsRequest): ResponseEntity<ProfileDto> {
        return ResponseEntity.ok(profileService.addWins(request))
    }

    @GetMapping("/me/xp-history")
    @Operation(summary = "Get XP history", description = "Returns recent XP transactions for the current user")
    fun getXpHistory(
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<List<XpHistoryDto>> {
        return ResponseEntity.ok(profileService.getXpHistory(limit))
    }
}


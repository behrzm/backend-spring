package com.codequest.dto

import com.codequest.domain.entity.Profile
import java.time.OffsetDateTime

data class ProfileDto(
    val id: String,
    val email: String?,
    val displayName: String?,
    val xp: Int,
    val level: Int,
    val streak: Int,
    val wins: Int,
    val avatarUrl: String?,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null
) {
    companion object {
        fun from(profile: Profile): ProfileDto {
            return ProfileDto(
                id = profile.id,
                email = profile.email,
                displayName = profile.displayName,
                xp = profile.xp,
                level = profile.level,
                streak = profile.streak,
                wins = profile.wins,
                avatarUrl = profile.avatarUrl,
                createdAt = profile.createdAt,
                updatedAt = profile.updatedAt
            )
        }
    }
}

data class UpdateProfileRequest(
    val displayName: String? = null,
    val avatarUrl: String? = null
)

data class AddXpRequest(
    val deltaXp: Int
)

data class AddWinsRequest(
    val deltaWins: Int = 1
)

data class LeaderboardEntryDto(
    val id: String,
    val displayName: String?,
    val xp: Int,
    val level: Int,
    val wins: Int
)


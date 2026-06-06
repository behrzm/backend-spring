package com.prolearn.codecraftfront.data.api

import com.google.gson.annotations.SerializedName

// =========== PROFILE ============
data class ProfileResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String?,
    @SerializedName("displayName")
    val displayName: String?,
    @SerializedName("xp")
    val xp: Int,
    @SerializedName("level")
    val level: Int,
    @SerializedName("streak")
    val streak: Int,
    @SerializedName("wins")
    val wins: Int,
    @SerializedName("elo")
    val elo: Int,
    @SerializedName("avatarUrl")
    val avatarUrl: String?,
    @SerializedName("lastRewardClaimDate")
    val lastRewardClaimDate: String?,
    @SerializedName("lastDailyChallengeDate")
    val lastDailyChallengeDate: String? = null,
    @SerializedName("hasUnreadNotifications")
    val hasUnreadNotifications: Boolean = false
)

data class UpdateProfileRequest(
    @SerializedName("displayName")
    val displayName: String?,
    @SerializedName("avatarUrl")
    val avatarUrl: String?
)

data class AddXpRequest(
    @SerializedName("deltaXp")
    val deltaXp: Int,
    @SerializedName("reason")
    val reason: String? = "level_complete"
)

// =========== FRIENDS ============
data class FriendResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("displayName")
    val displayName: String?,
    @SerializedName("xp")
    val xp: Int,
    @SerializedName("level")
    val level: Int,
    @SerializedName("elo")
    val elo: Int,
    @SerializedName("streak")
    val streak: Int,
    @SerializedName("wins")
    val wins: Int,
    @SerializedName("avatarUrl")
    val avatarUrl: String?,
    @SerializedName("learningLanguage")
    val learningLanguage: String?,
    @SerializedName("status")
    val status: String // PENDING, ACCEPTED
)

data class FriendRequestAction(
    @SerializedName("senderId")
    val senderId: String,
    @SerializedName("action")
    val action: String // ACCEPT, DECLINE
)

// =========== LEVEL PROGRESS ============
data class LevelProgressResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("language")
    val language: String,
    @SerializedName("track")
    val track: String,
    @SerializedName("levelId")
    val levelId: Int,
    @SerializedName("stars")
    val stars: Int?,
    @SerializedName("completed")
    val completed: Boolean?,
    @SerializedName("completedAt")
    val completedAt: String?,
    @SerializedName("savedCode")
    val savedCode: String? = null
)

data class LeaderboardResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("displayName")
    val displayName: String?,
    @SerializedName("xp")
    val xp: Int,
    @SerializedName("level")
    val level: Int,
    @SerializedName("elo")
    val elo: Int = 1000
)

data class UpdateLevelProgressRequest(
    @SerializedName("language")
    val language: String,
    @SerializedName("track")
    val track: String,
    @SerializedName("levelId")
    val levelId: Int,
    @SerializedName("stars")
    val stars: Int?,
    @SerializedName("completed")
    val completed: Boolean?,
    @SerializedName("solutionCode")
    val solutionCode: String? = null
)

data class XpHistoryResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("createdAt")
    val createdAt: String?
)
// WarMatchResponse удален отсюда, так как он есть в CodeQuestApi.kt

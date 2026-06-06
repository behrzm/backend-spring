package com.prolearn.codecraftfront.data

data class LeaderboardEntry(
    val userId: String,
    val displayName: String,
    val xp: Int,
    val level: Int,
    val wins: Int = 0,
    val streak: Int = 0
)

interface LeaderboardRepository {
    suspend fun fetchCurrentUser(userId: String): LeaderboardEntry?
    suspend fun fetchTop(limit: Long = 25): List<LeaderboardEntry>
    suspend fun upsertCurrentUser(userId: String, displayName: String, xp: Int, level: Int)
    suspend fun incrementCurrentUserXp(userId: String, displayName: String, deltaXp: Int, reason: String = "level_complete")
}

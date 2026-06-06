package com.prolearn.codecraftfront.data

import android.util.Log
import com.prolearn.codecraftfront.data.api.*
import com.prolearn.codecraftfront.data.api.ApiClient

class ApiLeaderboardRepository : LeaderboardRepository {
    private val api = ApiClient.api

    override suspend fun fetchCurrentUser(userId: String): LeaderboardEntry? {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                response.body()?.let { profile ->
                    LeaderboardEntry(
                        userId = profile.id,
                        displayName = profile.displayName ?: "Cyber Cadet",
                        xp = profile.xp,
                        level = profile.level,
                        wins = profile.wins,
                        streak = profile.streak
                    )
                }
            } else null
        } catch (e: Exception) {
            Log.e("ApiRepo", "fetchCurrentUser error: ${e.message}")
            null
        }
    }

    override suspend fun fetchTop(limit: Long): List<LeaderboardEntry> {
        return try {
            val response = api.getLeaderboard(limit.toInt())
            if (response.isSuccessful) {
                response.body()?.map { entry ->
                    LeaderboardEntry(
                        userId = entry.id,
                        displayName = entry.displayName ?: "Cyber Cadet",
                        xp = entry.xp,
                        level = entry.level
                    )
                } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e("ApiRepo", "fetchTop error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun upsertCurrentUser(userId: String, displayName: String, xp: Int, level: Int) {
        try {
            api.updateProfile(UpdateProfileRequest(displayName, null))
        } catch (_: Exception) {}
    }

    override suspend fun incrementCurrentUserXp(userId: String, displayName: String, deltaXp: Int, reason: String) {
        try {
            api.addXp(AddXpRequest(deltaXp, reason))
        } catch (e: Exception) {
            Log.e("ApiRepo", "incrementXp error: ${e.message}")
        }
    }
}

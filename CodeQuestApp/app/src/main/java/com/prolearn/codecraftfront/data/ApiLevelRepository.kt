package com.prolearn.codecraftfront.data

import android.util.Log
import com.prolearn.codecraftfront.data.api.ApiClient
import com.prolearn.codecraftfront.data.api.LevelProgressResponse
import com.prolearn.codecraftfront.data.api.UpdateLevelProgressRequest

class ApiLevelRepository : LevelRepository {
    private val api = ApiClient.api

    override suspend fun getLevelProgress(language: String, track: String, levelId: Int): LevelProgressResponse? {
        return try {
            val response = api.getAllLevelProgress()
            if (response.isSuccessful) {
                response.body()?.find { it.language == language && it.track == track && it.levelId == levelId }
            } else null
        } catch (e: Exception) {
            Log.e("ApiLevelRepo", "Error fetching level progress: ${e.message}")
            null
        }
    }

    override suspend fun updateLevelProgress(
        language: String,
        track: String,
        levelId: Int,
        stars: Int,
        completed: Boolean,
        solutionCode: String?
    ): Boolean {
        return try {
            val response = api.updateLevelProgress(
                UpdateLevelProgressRequest(language, track, levelId, stars, completed, solutionCode)
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ApiLevelRepo", "Error updating level progress: ${e.message}")
            false
        }
    }
}

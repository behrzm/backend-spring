package com.prolearn.codecraftfront.data

import com.prolearn.codecraftfront.data.api.LevelProgressResponse

interface LevelRepository {
    suspend fun getLevelProgress(language: String, track: String, levelId: Int): LevelProgressResponse?
    suspend fun updateLevelProgress(
        language: String, 
        track: String, 
        levelId: Int, 
        stars: Int, 
        completed: Boolean,
        solutionCode: String? = null
    ): Boolean
}

package com.codequest.dto

import com.codequest.domain.entity.LevelProgress
import java.time.OffsetDateTime

data class LevelProgressDto(
    val id: Long,
    val userId: String,
    val language: String,
    val track: String,
    val levelId: Int,
    val stars: Int,
    val completed: Boolean,
    val completedAt: OffsetDateTime? = null
) {
    companion object {
        fun from(levelProgress: LevelProgress): LevelProgressDto {
            return LevelProgressDto(
                id = levelProgress.id,
                userId = levelProgress.userId,
                language = levelProgress.language,
                track = levelProgress.track,
                levelId = levelProgress.levelId,
                stars = levelProgress.stars,
                completed = levelProgress.completed,
                completedAt = levelProgress.completedAt
            )
        }
    }
}

data class UpdateLevelProgressRequest(
    val language: String,
    val track: String,
    val levelId: Int,
    val stars: Int,
    val completed: Boolean
)


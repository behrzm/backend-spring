package com.codequest.dto

import com.codequest.domain.entity.UserLearningProfile
import java.time.OffsetDateTime

data class LearningContextDto(
    val id: Long,
    val userId: String,
    val language: String,
    val track: String,
    val levelId: Int,
    val attempts: Int,
    val failedCommands: String?,
    val lastError: String?,
    val updatedAt: OffsetDateTime? = null
) {
    companion object {
        fun from(profile: UserLearningProfile): LearningContextDto {
            return LearningContextDto(
                id = profile.id,
                userId = profile.userId,
                language = profile.language,
                track = profile.track,
                levelId = profile.levelId,
                attempts = profile.attempts,
                failedCommands = profile.failedCommands,
                lastError = profile.lastError,
                updatedAt = profile.updatedAt
            )
        }
    }
}

data class UpdateLearningContextRequest(
    val language: String,
    val track: String,
    val levelId: Int,
    val failedCommands: String? = null,
    val lastError: String? = null
)


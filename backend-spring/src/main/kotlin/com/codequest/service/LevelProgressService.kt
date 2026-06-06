package com.codequest.service

import com.codequest.domain.entity.LevelProgress
import com.codequest.dto.LevelProgressDto
import com.codequest.dto.UpdateLevelProgressRequest
import com.codequest.repository.LevelProgressRepository
import com.codequest.security.SecurityContext
import io.github.microutils.kotlin.logging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

private val logger = KotlinLogging.logger {}

@Service
@Transactional
class LevelProgressService(
    private val levelProgressRepository: LevelProgressRepository
) {

    fun getLevelProgress(language: String, track: String, levelId: Int): LevelProgressDto? {
        val userId = SecurityContext.getUserId() ?: throw IllegalStateException("User not authenticated")
        
        val progress = levelProgressRepository.findByUserIdAndLanguageAndTrackAndLevelId(
            userId, language, track, levelId
        )
        
        return progress?.let { LevelProgressDto.from(it) }
    }

    fun updateLevelProgress(request: UpdateLevelProgressRequest): LevelProgressDto {
        val userId = SecurityContext.getUserId() ?: throw IllegalStateException("User not authenticated")
        
        var progress = levelProgressRepository.findByUserIdAndLanguageAndTrackAndLevelId(
            userId, request.language, request.track, request.levelId
        )
        
        if (progress == null) {
            progress = LevelProgress(
                userId = userId,
                language = request.language,
                track = request.track,
                levelId = request.levelId
            )
        }
        
        progress.stars = request.stars
        progress.completed = request.completed
        if (request.completed && progress.completedAt == null) {
            progress.completedAt = OffsetDateTime.now()
        }
        
        levelProgressRepository.save(progress)
        logger.info { "Updated level progress for user $userId: ${request.language} ${request.track} level ${request.levelId}" }
        
        return LevelProgressDto.from(progress)
    }
}


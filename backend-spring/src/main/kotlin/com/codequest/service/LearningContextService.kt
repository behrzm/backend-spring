package com.codequest.service

import com.codequest.domain.entity.UserLearningProfile
import com.codequest.dto.LearningContextDto
import com.codequest.dto.UpdateLearningContextRequest
import com.codequest.repository.UserLearningProfileRepository
import com.codequest.security.SecurityContext
import io.github.microutils.kotlin.logging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

private val logger = KotlinLogging.logger {}

@Service
@Transactional
class LearningContextService(
    private val userLearningProfileRepository: UserLearningProfileRepository
) {

    fun getLearningContext(language: String, track: String, levelId: Int): LearningContextDto? {
        val userId = SecurityContext.getUserId() ?: throw IllegalStateException("User not authenticated")
        
        val context = userLearningProfileRepository.findByUserIdAndLanguageAndTrackAndLevelId(
            userId, language, track, levelId
        )
        
        return context?.let { LearningContextDto.from(it) }
    }

    fun updateLearningContext(request: UpdateLearningContextRequest): LearningContextDto {
        val userId = SecurityContext.getUserId() ?: throw IllegalStateException("User not authenticated")
        
        var context = userLearningProfileRepository.findByUserIdAndLanguageAndTrackAndLevelId(
            userId, request.language, request.track, request.levelId
        )
        
        if (context == null) {
            context = UserLearningProfile(
                userId = userId,
                language = request.language,
                track = request.track,
                levelId = request.levelId
            )
        }
        
        context.attempts += 1
        request.failedCommands?.let { context.failedCommands = it }
        request.lastError?.let { context.lastError = it }
        context.updatedAt = OffsetDateTime.now()
        
        userLearningProfileRepository.save(context)
        logger.info { "Updated learning context for user $userId: ${request.language} ${request.track} level ${request.levelId}, attempts: ${context.attempts}" }
        
        return LearningContextDto.from(context)
    }
}


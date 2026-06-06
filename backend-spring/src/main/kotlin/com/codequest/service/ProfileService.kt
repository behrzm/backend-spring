package com.codequest.service

import com.codequest.domain.entity.Profile
import com.codequest.domain.entity.XpHistory
import com.codequest.dto.*
import com.codequest.repository.ProfileRepository
import com.codequest.repository.XpHistoryRepository
import com.codequest.security.SecurityContext
import io.github.microutils.kotlin.logging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

private val logger = KotlinLogging.logger {}

@Service
@Transactional
class ProfileService(
    private val profileRepository: ProfileRepository,
    private val xpHistoryRepository: XpHistoryRepository
) {

    fun getMyProfile(): ProfileDto {
        val userId = SecurityContext.getUserId() ?: throw IllegalStateException("User not authenticated")
        
        var profile = profileRepository.findById(userId)
        
        // Create profile if doesn't exist
        if (profile == null) {
            profile = Profile(
                id = userId,
                displayName = "Player"
            )
            profileRepository.save(profile)
            logger.info { "Created new profile for user: $userId" }
        }
        
        return ProfileDto.from(profile)
    }

    fun getTopPlayers(limit: Int): List<LeaderboardEntryDto> {
        val pageRequest = PageRequest.of(0, limit.coerceAtMost(100))
        val topPlayers = profileRepository.findTopPlayers(pageRequest)
        
        return topPlayers.map {
            LeaderboardEntryDto(
                id = it.id,
                displayName = it.displayName,
                xp = it.xp,
                level = it.level,
                wins = it.wins
            )
        }
    }

    fun updateMyProfile(request: UpdateProfileRequest): ProfileDto {
        val userId = SecurityContext.getUserId() ?: throw IllegalStateException("User not authenticated")
        
        val profile = profileRepository.findById(userId) 
            ?: throw IllegalStateException("Profile not found")
        
        request.displayName?.let { profile.displayName = it }
        request.avatarUrl?.let { profile.avatarUrl = it }
        profile.updatedAt = OffsetDateTime.now()
        
        profileRepository.save(profile)
        logger.info { "Updated profile for user: $userId" }
        
        return ProfileDto.from(profile)
    }

    fun addXp(request: AddXpRequest): ProfileDto {
        val userId = SecurityContext.getUserId() ?: throw IllegalStateException("User not authenticated")
        
        if (request.deltaXp <= 0) {
            throw IllegalArgumentException("deltaXp must be greater than 0")
        }
        
        val profile = profileRepository.findById(userId)
            ?: throw IllegalStateException("Profile not found")
        
        val newXp = profile.xp + request.deltaXp
        val newLevel = com.codequest.util.LevelProgression.levelFromTotalXp(newXp)
        
        profile.xp = newXp
        profile.level = newLevel
        profile.updatedAt = OffsetDateTime.now()
        
        profileRepository.save(profile)
        
        // Record in XP history
        val xpHistoryEntry = XpHistory(
            userId = userId,
            amount = request.deltaXp,
            reason = "level_complete"
        )
        xpHistoryRepository.save(xpHistoryEntry)
        
        logger.info { "Added $request.deltaXp XP to user $userId, new level: $newLevel" }
        
        return ProfileDto.from(profile)
    }

    fun addWins(request: AddWinsRequest): ProfileDto {
        val userId = SecurityContext.getUserId() ?: throw IllegalStateException("User not authenticated")
        
        val profile = profileRepository.findById(userId)
            ?: throw IllegalStateException("Profile not found")
        
        profile.wins += request.deltaWins
        profile.updatedAt = OffsetDateTime.now()
        
        profileRepository.save(profile)
        logger.info { "Added $request.deltaWins wins to user $userId" }
        
        return ProfileDto.from(profile)
    }

    fun getXpHistory(limit: Int): List<XpHistoryDto> {
        val userId = SecurityContext.getUserId() ?: throw IllegalStateException("User not authenticated")
        
        val pageRequest = PageRequest.of(0, limit.coerceAtMost(100))
        val history = xpHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageRequest)
        
        return history.map { XpHistoryDto.from(it) }
    }
}


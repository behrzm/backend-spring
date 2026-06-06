package com.codequest.service

import com.codequest.domain.entity.LevelProgress
import com.codequest.dto.UpdateLevelProgressRequest
import com.codequest.repository.LevelProgressRepository
import com.codequest.security.SecurityContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LevelProgressServiceTest {

    private lateinit var levelProgressRepository: LevelProgressRepository
    private lateinit var levelProgressService: LevelProgressService

    @BeforeEach
    fun setUp() {
        levelProgressRepository = mock()
        levelProgressService = LevelProgressService(levelProgressRepository)
    }

    @Test
    fun `getLevelProgress should return null if progress not found`() {
        val userId = "test-user"
        SecurityContext.setUserId(userId)

        whenever(
            levelProgressRepository.findByUserIdAndLanguageAndTrackAndLevelId(
                userId, "python", "beginner", 1
            )
        ).thenReturn(null)

        val result = levelProgressService.getLevelProgress("python", "beginner", 1)

        assertEquals(null, result)
    }

    @Test
    fun `updateLevelProgress should create new progress if not exists`() {
        val userId = "test-user"
        SecurityContext.setUserId(userId)

        whenever(
            levelProgressRepository.findByUserIdAndLanguageAndTrackAndLevelId(
                userId, "python", "beginner", 1
            )
        ).thenReturn(null)

        whenever(levelProgressRepository.save(any())).thenAnswer { it.arguments[0] }

        val request = UpdateLevelProgressRequest(
            language = "python",
            track = "beginner",
            levelId = 1,
            stars = 2,
            completed = false
        )

        val result = levelProgressService.updateLevelProgress(request)

        assertNotNull(result)
        assertEquals(2, result.stars)
        assertEquals(false, result.completed)
        verify(levelProgressRepository).save(any())
    }

    @Test
    fun `updateLevelProgress should mark completed_at when completing level`() {
        val userId = "test-user"
        SecurityContext.setUserId(userId)

        val existingProgress = LevelProgress(
            userId = userId,
            language = "python",
            track = "beginner",
            levelId = 1,
            stars = 0,
            completed = false
        )

        whenever(
            levelProgressRepository.findByUserIdAndLanguageAndTrackAndLevelId(
                userId, "python", "beginner", 1
            )
        ).thenReturn(existingProgress)

        whenever(levelProgressRepository.save(any())).thenAnswer { it.arguments[0] }

        val request = UpdateLevelProgressRequest(
            language = "python",
            track = "beginner",
            levelId = 1,
            stars = 3,
            completed = true
        )

        val result = levelProgressService.updateLevelProgress(request)

        assertEquals(true, result.completed)
        assertNotNull(result.completedAt)
    }
}


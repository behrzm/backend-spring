package com.codequest.service

import com.codequest.domain.entity.UserLearningProfile
import com.codequest.dto.UpdateLearningContextRequest
import com.codequest.repository.UserLearningProfileRepository
import com.codequest.security.SecurityContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LearningContextServiceTest {

    private lateinit var userLearningProfileRepository: UserLearningProfileRepository
    private lateinit var learningContextService: LearningContextService

    @BeforeEach
    fun setUp() {
        userLearningProfileRepository = mock()
        learningContextService = LearningContextService(userLearningProfileRepository)
    }

    @Test
    fun `updateLearningContext should create new context if not exists`() {
        val userId = "test-user"
        SecurityContext.setUserId(userId)

        whenever(
            userLearningProfileRepository.findByUserIdAndLanguageAndTrackAndLevelId(
                userId, "python", "beginner", 1
            )
        ).thenReturn(null)

        whenever(userLearningProfileRepository.save(any())).thenAnswer { it.arguments[0] }

        val request = UpdateLearningContextRequest(
            language = "python",
            track = "beginner",
            levelId = 1,
            failedCommands = "add(a b)",
            lastError = "missing colon"
        )

        val result = learningContextService.updateLearningContext(request)

        assertNotNull(result)
        assertEquals(1, result.attempts)
        assertEquals("add(a b)", result.failedCommands)
        assertEquals("missing colon", result.lastError)
        verify(userLearningProfileRepository).save(any())
    }

    @Test
    fun `updateLearningContext should increment attempts`() {
        val userId = "test-user"
        SecurityContext.setUserId(userId)

        val existingContext = UserLearningProfile(
            userId = userId,
            language = "python",
            track = "beginner",
            levelId = 1,
            attempts = 2
        )

        whenever(
            userLearningProfileRepository.findByUserIdAndLanguageAndTrackAndLevelId(
                userId, "python", "beginner", 1
            )
        ).thenReturn(existingContext)

        whenever(userLearningProfileRepository.save(any())).thenAnswer { it.arguments[0] }

        val request = UpdateLearningContextRequest(
            language = "python",
            track = "beginner",
            levelId = 1,
            failedCommands = "new error",
            lastError = "syntax error"
        )

        val result = learningContextService.updateLearningContext(request)

        assertEquals(3, result.attempts)
    }
}


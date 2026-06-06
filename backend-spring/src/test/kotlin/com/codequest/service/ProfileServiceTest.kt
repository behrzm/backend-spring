package com.codequest.service

import com.codequest.domain.entity.Profile
import com.codequest.dto.AddXpRequest
import com.codequest.dto.UpdateProfileRequest
import com.codequest.repository.ProfileRepository
import com.codequest.repository.XpHistoryRepository
import com.codequest.security.SecurityContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class ProfileServiceTest {

    private lateinit var profileRepository: ProfileRepository
    private lateinit var xpHistoryRepository: XpHistoryRepository
    private lateinit var profileService: ProfileService

    @BeforeEach
    fun setUp() {
        profileRepository = mock()
        xpHistoryRepository = mock()
        profileService = ProfileService(profileRepository, xpHistoryRepository)
    }

    @Test
    fun `getMyProfile should create profile if not exists`() {
        val userId = "test-user-123"
        SecurityContext.setUserId(userId)

        whenever(profileRepository.findById(userId)).thenReturn(null)
        whenever(profileRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = profileService.getMyProfile()

        assertEquals(userId, result.id)
        assertEquals("Player", result.displayName)
        assertEquals(0, result.xp)
        assertEquals(1, result.level)
        verify(profileRepository).save(any())
    }

    @Test
    fun `addXp should calculate correct level`() {
        val userId = "test-user-123"
        SecurityContext.setUserId(userId)

        val existingProfile = Profile(
            id = userId,
            displayName = "TestPlayer",
            xp = 0,
            level = 1
        )

        whenever(profileRepository.findById(userId)).thenReturn(existingProfile)
        whenever(profileRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = profileService.addXp(AddXpRequest(deltaXp = 400))

        assertEquals(400, result.xp)
        assertEquals(2, result.level)
    }

    @Test
    fun `addXp with negative deltaXp should throw exception`() {
        val userId = "test-user-123"
        SecurityContext.setUserId(userId)

        assertThrows<IllegalArgumentException> {
            profileService.addXp(AddXpRequest(deltaXp = -100))
        }
    }

    @Test
    fun `updateProfile should update display name and avatar`() {
        val userId = "test-user-123"
        SecurityContext.setUserId(userId)

        val existingProfile = Profile(
            id = userId,
            displayName = "OldName",
            avatarUrl = null
        )

        whenever(profileRepository.findById(userId)).thenReturn(existingProfile)
        whenever(profileRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = profileService.updateMyProfile(
            UpdateProfileRequest(
                displayName = "NewName",
                avatarUrl = "https://example.com/avatar.png"
            )
        )

        assertEquals("NewName", result.displayName)
        assertEquals("https://example.com/avatar.png", result.avatarUrl)
    }
}


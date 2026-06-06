package com.codequest.service;

import com.codequest.domain.entity.Profile;
import com.codequest.dto.AddXpRequest;
import com.codequest.dto.UpdateProfileRequest;
import com.codequest.repository.FriendshipRepository;
import com.codequest.repository.ProfileRepository;
import com.codequest.repository.XpHistoryRepository;
import com.codequest.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private XpHistoryRepository xpHistoryRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profileService = new ProfileService(profileRepository, xpHistoryRepository, friendshipRepository);
    }

    @Test
    void testGetMyProfileCreatesProfileIfNotExists() {
        String userId = "test-user-123";
        SecurityContext.setUserId(userId);

        when(profileRepository.findById(userId)).thenReturn(Optional.empty());
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = profileService.getMyProfile();

        assertEquals(userId, result.getId());
        assertEquals("Player", result.getDisplayName());
        assertEquals(0, result.getXp());
        assertEquals(1, result.getLevel());
        verify(profileRepository).save(any());
    }

    @Test
    void testAddXpCalculatesCorrectLevel() {
        String userId = "test-user-123";
        SecurityContext.setUserId(userId);

        Profile existingProfile = new Profile(userId, null, "TestPlayer");
        existingProfile.setXp(0);
        existingProfile.setLevel(1);

        when(profileRepository.findById(userId)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = profileService.addXp(new AddXpRequest(400));

        assertEquals(400, result.getXp());
        assertEquals(2, result.getLevel());
    }

    @Test
    void testAddXpLevelThreeNeeds600More() {
        String userId = "test-user-456";
        SecurityContext.setUserId(userId);

        Profile existingProfile = new Profile(userId, null, "TestPlayer");
        existingProfile.setXp(400);
        existingProfile.setLevel(2);

        when(profileRepository.findById(userId)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = profileService.addXp(new AddXpRequest(600));

        assertEquals(1000, result.getXp());
        assertEquals(3, result.getLevel());
    }

    @Test
    void testAddXpWithNegativeDeltaThrowsException() {
        String userId = "test-user-123";
        SecurityContext.setUserId(userId);

        assertThrows(IllegalArgumentException.class, () -> {
            profileService.addXp(new AddXpRequest(-100));
        });
    }

    @Test
    void testUpdateProfileUpdatesDisplayNameAndAvatar() {
        String userId = "test-user-123";
        SecurityContext.setUserId(userId);

        Profile existingProfile = new Profile(userId, null, "OldName");
        existingProfile.setAvatarUrl(null);

        when(profileRepository.findById(userId)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = profileService.updateMyProfile(
                new UpdateProfileRequest("NewName", "https://example.com/avatar.png")
        );

        assertEquals("NewName", result.getDisplayName());
        assertEquals("https://example.com/avatar.png", result.getAvatarUrl());
    }
}


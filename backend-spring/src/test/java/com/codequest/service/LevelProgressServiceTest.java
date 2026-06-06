package com.codequest.service;

import com.codequest.domain.entity.LevelProgress;
import com.codequest.dto.UpdateLevelProgressRequest;
import com.codequest.repository.LevelProgressRepository;
import com.codequest.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LevelProgressServiceTest {

    @Mock
    private LevelProgressRepository levelProgressRepository;

    private LevelProgressService levelProgressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        levelProgressService = new LevelProgressService(levelProgressRepository);
    }

    @Test
    void testGetLevelProgressReturnsEmptyIfNotFound() {
        String userId = "test-user";
        SecurityContext.setUserId(userId);

        when(levelProgressRepository.findByUserIdAndLanguageAndTrackAndLevelId(userId, "python", "beginner", 1))
                .thenReturn(Optional.empty());

        var result = levelProgressService.getLevelProgress("python", "beginner", 1);

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateLevelProgressCreatesNewIfNotExists() {
        String userId = "test-user";
        SecurityContext.setUserId(userId);

        when(levelProgressRepository.findByUserIdAndLanguageAndTrackAndLevelId(userId, "python", "beginner", 1))
                .thenReturn(Optional.empty());
        when(levelProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new UpdateLevelProgressRequest("python", "beginner", 1, 2, false);
        var result = levelProgressService.updateLevelProgress(request);

        assertNotNull(result);
        assertEquals(2, result.getStars());
        assertEquals(false, result.getCompleted());
        verify(levelProgressRepository).save(any());
    }

    @Test
    void testUpdateLevelProgressMarkCompletedAtWhenCompleting() {
        String userId = "test-user";
        SecurityContext.setUserId(userId);

        LevelProgress existingProgress = new LevelProgress(userId, "python", "beginner", 1);
        existingProgress.setStars(0);
        existingProgress.setCompleted(false);

        when(levelProgressRepository.findByUserIdAndLanguageAndTrackAndLevelId(userId, "python", "beginner", 1))
                .thenReturn(Optional.of(existingProgress));
        when(levelProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new UpdateLevelProgressRequest("python", "beginner", 1, 3, true);
        var result = levelProgressService.updateLevelProgress(request);

        assertEquals(true, result.getCompleted());
        assertNotNull(result.getCompletedAt());
    }
}

